import { withModuleFederation } from '@nx/module-federation/angular';
import config from './module-federation.config';

export default withModuleFederation(
  {
    ...config,
    remotes: [
      ['mfeExplore', 'https://tractor-store-explore.vercel.app/remoteEntry.mjs'],
      ['mfeDecide', 'https://tractor-store-decide.vercel.app/remoteEntry.mjs'],
      ['mfeCheckout', 'https://tractor-store-checkout.vercel.app/remoteEntry.mjs'],
    ],
  },
  { dts: false }
);
