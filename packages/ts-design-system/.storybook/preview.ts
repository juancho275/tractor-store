import type { Preview } from '@storybook/angular';

const preview: Preview = {
  parameters: {
    backgrounds: {
      options: {
        light: { name: 'light', value: '#f9fafb' },
        dark: { name: 'dark', value: '#111827' }
      }
    },
  },

  decorators: [],

  initialGlobals: {
    backgrounds: {
      value: 'light'
    }
  }
};

export default preview;