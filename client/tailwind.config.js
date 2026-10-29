/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        crm: {
          navy: '#0B132B',
          darkBlue: '#1C2541',
          slate: '#3A506B',
          teal: '#5BC0BE',
          ice: '#6FFFE9',
          purple: '#6366F1',
          violet: '#8B5CF6',
          bg: '#F8FAFC',
          card: '#FFFFFF',
          border: '#E2E8F0',
        }
      }
    },
  },
  plugins: [],
}
