import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';

/**
 * Vite configuration for SpringTwin Vue.js frontend.
 * Output is configured to Spring Boot resources/public directory.
 */
export default defineConfig({
  plugins: [vue()],
  
  // Base path for production build
  base: '/',
  
  // Resolve aliases for imports
  resolve: {
    alias: {
      '@': resolve(__dirname, '.'),
      '@app': resolve(__dirname, 'app'),
      '@project': resolve(__dirname, 'project'),
      '@architecture': resolve(__dirname, 'architecture'),
      '@analysis': resolve(__dirname, 'analysis'),
      '@report': resolve(__dirname, 'report'),
      '@mcp': resolve(__dirname, 'mcp'),
    },
  },
  
  // Build configuration
  build: {
    // Output directory: Gradle build directory for Spring Boot
    outDir: resolve(__dirname, '../../../build/resources/main/public'),
    
    // Empty output directory before build
    emptyOutDir: true,
    
    // Generate source maps for debugging
    sourcemap: false,
    
    // Rollup options
    rollupOptions: {
      output: {
        // JS files go to js/ subdirectory
        entryFileNames: 'js/[name].[hash].js',
        chunkFileNames: 'js/[name].[hash].js',
        assetFileNames: (assetInfo) => {
          const name = assetInfo.name || '';
          if (/\.css$/.test(name)) {
            return 'css/[name].[hash][extname]';
          }
          if (/\.(png|jpe?g|svg|gif|woff2?|eot|ttf|otf)$/.test(name)) {
            return 'asset/[name].[hash][extname]';
          }
          return '[name].[hash][extname]';
        },
      },
    },
  },
  
  // Development server configuration
  server: {
    // Port for development server
    port: 5173,
    
    // Proxy API requests to Spring Boot backend
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  
  // Test configuration
  test: {
    globals: true,
    environment: 'jsdom',
  },
});