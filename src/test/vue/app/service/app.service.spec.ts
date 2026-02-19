/**
 * Unit tests for app.service.ts
 * Tests the application service utility functions.
 */
import { describe, it, expect, vi } from 'vitest';
import { formatAppTitle, isProductionMode, isDevelopmentMode, isTestMode } from '@/app/service/app.service';

describe('app.service', () => {
  describe('formatAppTitle', () => {
    /**
     * Test: formatAppTitle formats correctly
     * Expected: Returns formatted string with version
     */
    it('should format app title with version', () => {
      const result = formatAppTitle('Spring Twin', '1.0.0');
      expect(result).toBe('Spring Twin v1.0.0');
    });

    /**
     * Test: formatAppTitle handles different versions
     * Expected: Returns formatted string with different version
     */
    it('should handle different version formats', () => {
      const result = formatAppTitle('Spring Twin', '2.0.0-beta');
      expect(result).toBe('Spring Twin v2.0.0-beta');
    });
  });

  describe('isProductionMode', () => {
    /**
     * Test: isProductionMode returns a boolean
     * Expected: Returns a boolean value
     * Note: import.meta.env.PROD is a compile-time constant set by Vite,
     * it cannot be changed at runtime via vi.stubEnv()
     */
    it('should return a boolean value', () => {
      const result = isProductionMode();
      expect(typeof result).toBe('boolean');
    });

    /**
     * Test: isProductionMode returns false in test environment
     * Expected: Returns false when running tests (not production build)
     */
    it('should return false in test environment', () => {
      // In test mode, PROD is always false since it's not a production build
      expect(isProductionMode()).toBe(false);
    });
  });

  describe('isDevelopmentMode', () => {
    /**
     * Test: isDevelopmentMode returns a boolean
     * Expected: Returns a boolean value
     * Note: import.meta.env.DEV is a compile-time constant set by Vite,
     * it cannot be changed at runtime via vi.stubEnv()
     */
    it('should return a boolean value', () => {
      const result = isDevelopmentMode();
      expect(typeof result).toBe('boolean');
    });

    /**
     * Test: isDevelopmentMode returns true in vitest environment
     * Expected: Returns true when running vitest (vitest runs in dev mode by default)
     */
    it('should return true in vitest environment', () => {
      // Vitest runs in dev mode by default, so DEV is true
      expect(isDevelopmentMode()).toBe(true);
    });
  });

  describe('isTestMode', () => {
    /**
     * Test: isTestMode returns true when in test mode
     * Expected: Returns true when MODE is 'test'
     */
    it('should return true when in test mode', () => {
      vi.stubEnv('MODE', 'test');
      expect(isTestMode()).toBe(true);
      vi.unstubAllEnvs();
    });

    /**
     * Test: isTestMode returns false when not in test mode
     * Expected: Returns false when MODE is not 'test'
     */
    it('should return false when not in test mode', () => {
      vi.stubEnv('MODE', 'development');
      expect(isTestMode()).toBe(false);
      vi.unstubAllEnvs();
    });
  });
});