import type { Meta, StoryObj } from '@storybook/angular';
import { ButtonComponent } from './button';

const meta: Meta<ButtonComponent> = {
  title: 'Design System/Button',
  component: ButtonComponent,
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: 'select',
      options: ['primary', 'secondary', 'ghost', 'danger'],
    },
    size: {
      control: 'select',
      options: ['sm', 'md', 'lg'],
    },
    disabled: { control: 'boolean' },
    loading: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<ButtonComponent>;

export const Primary: Story = {
  args: { variant: 'primary', size: 'md' },
  render: (args) => ({
    props: args,
    template: `<ts-button [variant]="variant" [size]="size" [disabled]="disabled" [loading]="loading">Ver catálogo</ts-button>`,
  }),
};

export const Secondary: Story = {
  args: { variant: 'secondary', size: 'md' },
  render: (args) => ({
    props: args,
    template: `<ts-button [variant]="variant" [size]="size">Encontrar tienda</ts-button>`,
  }),
};

export const Ghost: Story = {
  args: { variant: 'ghost', size: 'md' },
  render: (args) => ({
    props: args,
    template: `<ts-button [variant]="variant" [size]="size">Más info</ts-button>`,
  }),
};

export const Loading: Story = {
  args: { variant: 'primary', size: 'md', loading: true },
  render: (args) => ({
    props: args,
    template: `<ts-button [variant]="variant" [size]="size" [loading]="loading">Guardando...</ts-button>`,
  }),
};

export const AllSizes: Story = {
  render: () => ({
    template: `
      <div style="display:flex;gap:12px;align-items:center">
        <ts-button variant="primary" size="sm">Pequeño</ts-button>
        <ts-button variant="primary" size="md">Mediano</ts-button>
        <ts-button variant="primary" size="lg">Grande</ts-button>
      </div>
    `,
  }),
};