import { test, expect } from '@playwright/test';
import {
  mockAllApis, mockCartApis, loginViaLocalStorage, setSessionId,
} from './fixtures/api-setup';
import { PRODUCT_ID, order } from './fixtures/mock-data';

test.describe('Flujo completo de compra', () => {

  test('catálogo → producto → carrito → login → checkout → confirmación', async ({ page }) => {
    await mockAllApis(page);

    // 1. Navegar al catálogo
    await page.goto('/explore');
    await expect(page.locator('.catalog__grid, .product-card').first()).toBeVisible({ timeout: 10000 });

    // 2. Abrir detalle del producto
    await page.locator('.product-card').first().click();
    await expect(page).toHaveURL(new RegExp(`/decide/${PRODUCT_ID}`));
    await expect(page.locator('h1, .product-detail__name')).toBeVisible({ timeout: 10000 });

    // 3. Agregar al carrito
    await page.getByRole('button', { name: /agregar al carrito/i }).click();
    // El badge del carrito en el header debe actualizarse
    await expect(page.locator('.header__cart-badge')).toBeVisible({ timeout: 5000 });

    // 4. Ir al checkout — debe redirigir a login porque no hay sesión
    await page.locator('.header__cart').click();
    await expect(page).toHaveURL(/\/auth\/login/);

    // 5. Hacer login
    await page.fill('#email', 'e2e@tractorstore.com');
    await page.fill('#password', 'password123');
    await page.getByRole('button', { name: 'Ingresar' }).click();

    // 6. Debe volver al checkout con el carrito cargado
    await expect(page).toHaveURL(/\/checkout/);
    await expect(page.locator('.checkout__item, .checkout__items').first()).toBeVisible({ timeout: 10000 });

    // 7. El email viene pre-llenado desde la sesión
    await expect(page.locator('input[formControlName="customerEmail"]'))
      .toHaveValue('e2e@tractorstore.com');

    // 8. Completar datos del pedido
    await page.fill('input[formControlName="customerName"]', 'E2E Tester');
    await page.fill('textarea[formControlName="shippingAddress"]', 'Calle 123 #45-67, Cali, Valle del Cauca');

    // 9. Confirmar pedido
    await page.getByRole('button', { name: /confirmar pedido/i }).click();

    // 10. Ver página de confirmación
    await expect(page).toHaveURL(/\/checkout\/confirmation/);
    await expect(page.locator('.confirmation__order-number, .confirmation__hero')).toBeVisible({ timeout: 10000 });
    await expect(page.getByText(order.orderNumber)).toBeVisible();
  });

  test('mis pedidos muestra el historial de órdenes del usuario', async ({ page }) => {
    await mockAllApis(page);
    await loginViaLocalStorage(page);

    await page.goto('/orders');

    // Debe mostrar la orden mockeada
    await expect(page.getByText(order.orderNumber)).toBeVisible({ timeout: 10000 });
    await expect(page.locator('.order-card__status--pending')).toBeVisible();

    // Expandir la orden para ver el detalle
    await page.locator('.order-card__header').first().click();
    await expect(page.locator('.order-card__body')).toBeVisible();
    await expect(page.getByText('TractorPro X200')).toBeVisible();
  });

  test('carrito vacío muestra estado vacío con link al catálogo', async ({ page }) => {
    await mockAllApis(page);
    await loginViaLocalStorage(page);
    await setSessionId(page);

    await page.goto('/checkout');

    await expect(page.locator('.checkout__empty')).toBeVisible({ timeout: 10000 });
    await expect(page.getByRole('link', { name: /ver catálogo/i })).toBeVisible();
  });

  test('mis pedidos sin órdenes muestra estado vacío', async ({ page }) => {
    // Override del mock para devolver lista vacía
    await page.route('**/api/orders/customer/**', route =>
      route.fulfill({ json: [] })
    );
    await loginViaLocalStorage(page);

    await page.goto('/orders');

    await expect(page.locator('.orders-page__empty')).toBeVisible({ timeout: 10000 });
    await expect(page.getByRole('link', { name: /ver catálogo/i })).toBeVisible();
  });
});
