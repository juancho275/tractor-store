import { fileURLToPath } from "node:url";
import { dirname } from "node:path";
import type { StorybookConfig } from "@storybook/angular";

const config: StorybookConfig = {
  stories: ["../src/**/*.stories.ts"],
  addons: [getAbsolutePath("@chromatic-com/storybook")],
  framework: {
    name: getAbsolutePath("@storybook/angular"),
    options: {
      angularBrowserTarget: "ts-design-system:build-storybook",
    },
  },
  docs: {},
};

export default config;

function getAbsolutePath(value: string): any {
  return dirname(fileURLToPath(import.meta.resolve(`${value}/package.json`)));
}