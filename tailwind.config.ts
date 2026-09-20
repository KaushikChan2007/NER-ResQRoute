import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        earth: {
          50: "#FAF8F5",
          100: "#F3ECE4",
          200: "#E6D7C7",
          300: "#D4BFA8",
          400: "#BC9F80",
          500: "#A3805B",
          600: "#8A6543",
          700: "#6F4F33",
          800: "#543A25",
          900: "#392518",
          950: "#24160E",
        },
        luxe: {
          copper: "#B86234",
          bronze: "#965A3E",
          gold: "#C69C6D",
          sand: "#EBDCCB",
          espresso: "#1C1411",
          obsidian: "#120D0B",
        },
        risk: {
          green: "#1B8A5A",
          amber: "#D97706",
          red: "#C53030",
        }
      },
      fontFamily: {
        sans: ["system-ui", "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "sans-serif"],
        mono: ["ui-monospace", "SFMono-Regular", "Menlo", "Monaco", "Consolas", "monospace"],
      }
    },
  },
  plugins: [],
};
export default config;
