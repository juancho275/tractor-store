import { test, expect } from '@playwright/test';
import { mockCatalogApis } from './fixtures/api-setup';

test.describe('Página de inicio', () => {

  test.beforeEach(async ({ page }) => {
    await mockCatalogApis(page);
    await page.goto('/');
  });

  test('muestra el logo y título de la tienda', async ({ page }) => {
    await expect(page.locator('.header__logo-text')).toContainText('Tractor Store');
  });

  test('el header tiene el link al catálogo', async ({ page }) => {
    await expect(page.getByRole('link', { name: 'Catálogo' })).toBeVisible();
  });

  test('el header muestra Entrar cuando no hay sesión', async ({ page }) => {
    await expect(page.getByRole('link', { name: 'Entrar' })).toBeVisible();
  });

  test('navegar al catálogo desde el header', async ({ page }) => {
    await page.getByRole('link', { name: 'Catálogo' }).click();
    await expect(page).toHaveURL(/\/explore/);
  });

  test('el header muestra Mis pedidos y Salir cuando hay sesión activa', async ({ page }) => {
    await page.evaluate((userData) => {
      localStorage.setItem('ts-auth-token', 'mock-token');
      localStorage.setItem('ts-auth-user', JSON.stringify(userData));
    }, { email: 'e2e@tractorstore.com', role: 'USER' });

    await page.reload();

    await expect(page.getByRole('link', { name: 'Mis pedidos' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Salir' })).toBeVisible();
  });
});
