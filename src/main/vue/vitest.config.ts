import { defineConfig } from 'vitest/config';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';

/**
 * Vitest configuration for SpringTwin Vue.js frontend.
 * Supports unit testing of Vue components and TypeScript code.
 *
 * Run from src/main/vue directory: cd src/main/vue && set CI=true && npm run test
 */
export default defineConfig({
  plugins: [vue()],
   
  // Set root to project root to resolve modules correctly
  root: '../../../',
   
  // Allow access to all project files
  server: {
    fs: {
      allow: ['.']
    }
  },
    
  // Test configuration
  test: {
    // Use Jest-compatible syntax
    globals: true,
    
    // Use jsdom for DOM simulation
    environment: 'jsdom',
    
    // Include test files pattern (relative to project root)
    include: ['src/test/vue/**/*.spec.ts'],
    
    // Coverage configuration
    coverage: {
      // Use v8 provider for faster coverage
      provider: 'v8',
      
      // Explicitly specify the coverage v8 module
      v8: {
        'src/main/vue/**/*.ts': true,
        'src/main/vue/**/*.vue': true
      },
      
      // Use vitest coverage v8
      reporter: ['text', 'json', 'html', 'lcov', 'cobertura'],
      
      // Output directory for coverage reports
      reportsDirectory: '../../../build/reports/vue',
      
      // Coverage thresholds
      lines: 50,
      functions: 50,
      branches: 50,
      statements: 50,
      
      // Include specific files in coverage (relative to project root)
      include: [
        'src/main/vue/**/*.ts',
        'src/main/vue/**/*.vue',
      ],
      
      // Exclude from coverage
      exclude: [
        'src/main/vue/main.ts',
        'src/main/vue/vite-env.d.ts',
        'src/main/vue/**/*.d.ts'
      ],
    },
    
    // Pool configuration for Vite
    pool: 'forks',
    
    // Environment variables
    env: {
      NODE_ENV: 'test',
    },
    
    // Handle external dependencies - force resolving from local node_modules
    deps: {
      optimizer: {
        web: {
          include: ['@vue/test-utils', 'vue', 'vue-router', 'vuex'],
        },
      },
    },
    
    // Reporters configuration - default is console output
    reporters: ['default', 'junit'],
    
    // JUnit reporter configuration
    outputFile: 'build/reports/vue/junit.xml',
  },
   
  // Resolve aliases (absolute paths to src/main/vue subdirectories)
  // __dirname is src/main/vue, so aliases point to subdirectories within
  resolve: {
    alias: {
      '@': __dirname,
      '@app': resolve(__dirname, 'app'),
      '@project': resolve(__dirname, 'project'),
      '@architecture': resolve(__dirname, 'architecture'),
      '@analysis': resolve(__dirname, 'analysis'),
      '@report': resolve(__dirname, 'report'),
      '@mcp': resolve(__dirname, 'mcp'),
      // Resolve test dependencies from src/main/vue/node_modules
      'vitest': resolve(__dirname, 'node_modules/vitest'),
      '@vitest/coverage-v8': resolve(__dirname, 'node_modules/@vitest/coverage-v8'),
      '@vue/test-utils': resolve(__dirname, 'node_modules/@vue/test-utils'),
      'vue': resolve(__dirname, 'node_modules/vue'),
      'vue-router': resolve(__dirname, 'node_modules/vue-router'),
      'vuex': resolve(__dirname, 'node_modules/vuex'),
      'pinia': resolve(__dirname, 'node_modules/pinia'),
    },
  },
});