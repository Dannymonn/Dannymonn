/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        bg: '#0f0f0f',
        surface: '#1a1a1a',
        accent: '#ff6b35',
        'accent-red': '#e63946',
        'text-primary': '#f5f5f5',
        'text-muted': '#9e9e9e',
        divider: '#2a2a2a',
      }
    }
  },
  plugins: []
}
