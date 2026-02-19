/**
 * Application service utility functions.
 * Provides helper functions for the app module.
 */

/**
 * Formats the application title with version.
 * @param appName - The name of the application
 * @param version - The version string
 * @returns Formatted title with version
 */
export function formatAppTitle(appName: string, version: string): string {
  return `${appName} v${version}`;
}

/**
 * Determines if the application is in production mode.
 * @returns True if in production mode
 */
export function isProductionMode(): boolean {
  return import.meta.env.PROD === true;
}

/**
 * Determines if the application is in development mode.
 * @returns True if in development mode
 */
export function isDevelopmentMode(): boolean {
  return import.meta.env.DEV === true;
}

/**
 * Determines if the application is running in test mode.
 * @returns True if in test mode
 */
export function isTestMode(): boolean {
  return import.meta.env.MODE === 'test';
}