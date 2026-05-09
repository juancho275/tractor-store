import type { Meta, StoryObj } from '@storybook/angular';
import { PriceComponent } from './price';

const meta: Meta<PriceComponent> = {
  title: 'Design System/Price',
  component: PriceComponent,
  tags: ['autodocs'],
  argTypes: {
    amount: { control: 'number' },
    large: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<PriceComponent>;

export const Default: Story = {
  args: { amount: 45000000 },
};

export const Large: Story = {
  args: { amount: 120000000, large: true },
};

export const Accessory: Story = {
  args: { amount: 8500000 },
};