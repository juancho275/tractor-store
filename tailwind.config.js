/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './apps/**/*.{html,ts}',
    './packages/**/*.{html,ts}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50:  '#fef2f2',
          100: '#fee2e2',
          500: '#ef4444',
          600: '#dc2626',
          700: '#b91c1c',
        },
        tractor: {
          red:   '#CC2200',
          dark:  '#1a1a1a',
          steel: '#4a5568',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
