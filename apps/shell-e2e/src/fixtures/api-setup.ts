import { Page } from '@playwright/test';
import * as mock from './mock-data';

/** Mocks all backend API calls so tests run without a running backend. */
export async function mockAllApis(page: Page) {
  await mockCatalogApis(page);
  await mockInventoryApis(page);
  await mockAuthApis(page);
  await mockCartApis(page);
  await mockOrderApis(page);
}

export async function mockCatalogApis(page: Page) {
  await page.route('**/api/catalog/products?**', route =>
    route.fulfill({ json: mock.products })
  );
  await page.route('**/api/catalog/products', route =>
    route.fulfill({ json: mock.products })
  );
  await page.route(`**/api/catalog/products/${mock.PRODUCT_ID}`, route =>
    route.fulfill({ json: mock.productDetail })
  );
  await page.route('**/api/catalog/categories', route =>
    route.fulfill({ json: mock.categories })
  );
}

export async function mockInventoryApis(page: Page) {
  await page.route(`**/api/inventory/stock/${mock.VARIANT_ID}`, route =>
    route.fulfill({ json: mock.stock })
  );
  await page.route('**/api/inventory/stock/**', route =>
    route.fulfill({ json: mock.stock })
  );
}

export async function mockAuthApis(page: Page, failLogin = false) {
  await page.route('**/api/auth/login', route => {
    if (failLogin) {
      return route.fulfill({ status: 401, json: { message: 'Unauthorized' } });
    }
    return route.fulfill({ json: mock.authResponse });
  });
  await page.route('**/api/auth/register', route =>
    route.fulfill({ status: 201, json: mock.authResponse })
  );
}

export async function mockCartApis(page: Page, hasItem = false) {
  const cart = hasItem ? mock.cartWithItem : mock.emptyCart;
  await page.route('**/api/cart', route => {
    if (route.request().method() === 'DELETE') {
      return route.fulfill({ json: mock.emptyCart });
    }
    return route.fulfill({ json: cart });
  });
  await page.route('**/api/cart/items/**', route =>
    route.fulfill({ json: mock.cartWithItem })
  );
  await page.route('**/api/cart/items', route =>
    route.fulfill({ json: mock.cartWithItem })
  );
}

export async function mockOrderApis(page: Page) {
  await page.route('**/api/orders', route => {
    if (route.request().method() === 'POST') {
      return route.fulfill({ status: 201, json: mock.order });
    }
    return route.fulfill({ json: [mock.order] });
  });
  await page.route(`**/api/orders/customer/**`, route =>
    route.fulfill({ json: [mock.order] })
  );
}

/** Injects auth session into localStorage so the user appears logged in. */
export async function loginViaLocalStorage(page: Page) {
  await page.addInitScript(({ token, email }) => {
    localStorage.setItem('ts-auth-token', token);
    localStorage.setItem('ts-auth-user', JSON.stringify({ email, role: 'USER' }));
  }, { token: mock.USER_TOKEN, email: mock.USER_EMAIL });
}

/** Injects a cart session ID into localStorage. */
export async function setSessionId(page: Page) {
  await page.addInitScript((sessionId) => {
    localStorage.setItem('ts-session-id', sessionId);
  }, mock.SESSION_ID);
}
