/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./main.js",
    "./src/**/*.{scala, js}"
  ],
  theme: {
    extend: {
      maxHeight: {
        '128': '32rem'
      }
  },

  plugins: []
}
}