import type { Meta, StoryObj } from '@storybook/angular';
import { SpinnerComponent } from './spinner';

const meta: Meta<SpinnerComponent> = {
  title: 'Design System/Spinner',
  component: SpinnerComponent,
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<SpinnerComponent>;

export const AllSizes: Story = {
  render: () => ({
    template: `
      <div style="display:flex;gap:24px;align-items:center">
        <ts-spinner size="sm" />
        <ts-spinner size="md" />
        <ts-spinner size="lg" />
      </div>
    `,
  }),
};