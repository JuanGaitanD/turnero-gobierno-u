/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        brown: {
          50: '#fdf8f5',
          100: '#f5e9e1',
          200: '#e7d1bd',
          300: '#d7b293',
          400: '#c78f65',
          500: '#b6703e',
          600: '#9a5729',
          700: '#7a4320',
          800: '#5a2f17',
          900: '#3d1e0f'
        }
      }
    },
  },
  plugins: [],
}
