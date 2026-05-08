import type { Meta, StoryObj } from '@storybook/angular';
import { BadgeComponent } from './badge';

const meta: Meta<BadgeComponent> = {
  title: 'Design System/Badge',
  component: BadgeComponent,
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: 'select',
      options: ['success', 'warning', 'danger', 'info', 'neutral'],
    },
  },
};

export default meta;
type Story = StoryObj<BadgeComponent>;

export const AllVariants: Story = {
  render: () => ({
    template: `
      <div style="display:flex;gap:8px;flex-wrap:wrap">
        <ts-badge variant="success">En stock</ts-badge>
        <ts-badge variant="warning">Pocas unidades</ts-badge>
        <ts-badge variant="danger">Agotado</ts-badge>
        <ts-badge variant="info">Nuevo</ts-badge>
        <ts-badge variant="neutral">Sin clasificar</ts-badge>
      </div>
    `,
  }),
};