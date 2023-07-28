/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./main.js",
    "./front/**/*.{scala, js}"
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