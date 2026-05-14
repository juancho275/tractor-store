import { ModuleFederationConfig } from '@nx/module-federation';

const config: ModuleFederationConfig = {
  name: 'shell',
  remotes: [
    ['mfeExplore', 'https://tractor-store-explore.vercel.app'],
    ['mfeDecide', 'https://tractor-store-decide.vercel.app'],
    ['mfeCheckout', 'https://tractor-store-checkout.vercel.app'],
  ],
};

/**
 * Nx requires a default export of the config to allow correct resolution of the module federation graph.
 **/
export default config;
