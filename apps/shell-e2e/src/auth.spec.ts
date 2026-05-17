import { test, expect } from '@playwright/test';
import { mockAuthApis } from './fixtures/api-setup';

test.describe('Autenticación', () => {

  test.describe('Login', () => {

    test('muestra el formulario de login correctamente', async ({ page }) => {
      await page.goto('/auth/login');
      await expect(page.locator('h1')).toContainText('Iniciar sesión');
      await expect(page.locator('#email')).toBeVisible();
      await expect(page.locator('#password')).toBeVisible();
      await expect(page.getByRole('button', { name: 'Ingresar' })).toBeVisible();
    });

    test('login exitoso redirige al inicio', async ({ page }) => {
      await mockAuthApis(page);
      await page.goto('/auth/login');

      await page.fill('#email', 'e2e@tractorstore.com');
      await page.fill('#password', 'password123');
      await page.getByRole('button', { name: 'Ingresar' }).click();

      await expect(page).toHaveURL('/');
      await expect(page.getByRole('button', { name: 'Salir' })).toBeVisible();
    });

    test('login con credenciales incorrectas muestra error', async ({ page }) => {
      await mockAuthApis(page, true);
      await page.goto('/auth/login');

      await page.fill('#email', 'wrong@email.com');
      await page.fill('#password', 'wrongpassword');
      await page.getByRole('button', { name: 'Ingresar' }).click();

      await expect(page.locator('.auth-card__error')).toContainText('Credenciales incorrectas');
    });

    test('validación muestra errores en campos vacíos', async ({ page }) => {
      await page.goto('/auth/login');
      await page.getByRole('button', { name: 'Ingresar' }).click();

      await expect(page.locator('.auth-field__hint--error').first()).toBeVisible();
    });

    test('intento de ir a checkout sin sesión redirige a login', async ({ page }) => {
      await page.goto('/checkout');
      await expect(page).toHaveURL(/\/auth\/login\?returnUrl=%2Fcheckout/);
    });

    test('después del login regresa a la URL solicitada', async ({ page }) => {
      await mockAuthApis(page);
      await page.goto('/checkout');

      await page.fill('#email', 'e2e@tractorstore.com');
      await page.fill('#password', 'password123');
      await page.getByRole('button', { name: 'Ingresar' }).click();

      await expect(page).toHaveURL(/\/checkout/);
    });
  });

  test.describe('Registro', () => {

    test('muestra el formulario de registro correctamente', async ({ page }) => {
      await page.goto('/auth/register');
      await expect(page.locator('h1')).toContainText('Crear cuenta');
      await expect(page.locator('#email')).toBeVisible();
      await expect(page.locator('#password')).toBeVisible();
    });

    test('registro exitoso redirige al inicio', async ({ page }) => {
      await mockAuthApis(page);
      await page.goto('/auth/register');

      await page.fill('#email', 'nuevo@tractorstore.com');
      await page.fill('#password', 'password123');
      await page.getByRole('button', { name: 'Crear cuenta' }).click();

      await expect(page).toHaveURL('/');
    });

    test('validación rechaza contraseña menor a 8 caracteres', async ({ page }) => {
      await page.goto('/auth/register');

      await page.fill('#email', 'test@test.com');
      await page.fill('#password', 'corto');
      await page.getByRole('button', { name: 'Crear cuenta' }).click();

      await expect(page.locator('.auth-field__hint--error')).toContainText('Mínimo 8 caracteres');
    });

    test('el link lleva de login a registro y viceversa', async ({ page }) => {
      await page.goto('/auth/login');
      await page.getByRole('link', { name: 'Regístrate aquí' }).click();
      await expect(page).toHaveURL('/auth/register');

      await page.getByRole('link', { name: 'Inicia sesión aquí' }).click();
      await expect(page).toHaveURL('/auth/login');
    });
  });
});
