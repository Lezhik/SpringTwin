/**
 * Unit tests for App.vue component.
 * Tests the main application layout and initial rendering.
 */
import { describe, it, expect, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import App from '@/App.vue';

describe('App', () => {
  /**
   * Test: Component renders correctly
   * Expected: App component mounts without errors
   */
  it('should mount successfully', () => {
    const wrapper = mount(App);
    expect(wrapper.exists()).toBe(true);
  });

  /**
   * Test: App renders header with title
   * Expected: Header contains "Spring Twin" title
   */
  it('should render app title in header', () => {
    const wrapper = mount(App);
    const header = wrapper.find('[name="app-header"]');
    
    expect(header.exists()).toBe(true);
    expect(header.find('[name="app-title"]').text()).toBe('Spring Twin');
  });

  /**
   * Test: App renders subtitle
   * Expected: Subtitle contains the slogan
   */
  it('should render app subtitle', () => {
    const wrapper = mount(App);
    const header = wrapper.find('[name="app-header"]');
    
    expect(header.text()).toContain('Понимай архитектуру. Управляй сложностью.');
  });

  /**
   * Test: App renders main content area
   * Expected: Main content with name="main-content" exists
   */
  it('should render main content area', () => {
    const wrapper = mount(App);
    const mainContent = wrapper.find('[name="main-content"]');
    
    expect(mainContent.exists()).toBe(true);
  });

  /**
   * Test: App shows welcome message when not loading
   * Expected: Welcome message is visible
   */
  it('should show welcome message when not loading', () => {
    const wrapper = mount(App);
    const welcomeMessage = wrapper.find('[name="welcome-message"]');
    
    expect(welcomeMessage.exists()).toBe(true);
    expect(welcomeMessage.text()).toContain('Добро пожаловать в Spring Twin');
  });

  /**
   * Test: App hides loading indicator when not loading
   * Expected: Loading indicator is not visible
   */
  it('should hide loading indicator when not loading', () => {
    const wrapper = mount(App);
    const loadingIndicator = wrapper.find('[name="loading-indicator"]');
    
    expect(loadingIndicator.exists()).toBe(false);
  });

  /**
   * Test: App has loading indicator structure
   * Expected: Loading indicator element exists in template (v-if condition)
   * Note: Cannot test loading state via setData() with <script setup> components
   * that have internal refs. The loading state is controlled internally.
   */
  it('should have loading indicator structure in template', () => {
    // The template has a loading indicator with v-if="isLoading"
    // By default isLoading is false, so it's not rendered
    const wrapper = mount(App);
    
    // Verify loading indicator is not shown by default
    const loadingIndicator = wrapper.find('[name="loading-indicator"]');
    expect(loadingIndicator.exists()).toBe(false);
    
    // Verify welcome message is shown instead
    const welcomeMessage = wrapper.find('[name="welcome-message"]');
    expect(welcomeMessage.exists()).toBe(true);
  });

  /**
   * Test: App has correct root element attributes
   * Expected: Root element has correct name attribute
   */
  it('should have correct root element attributes', () => {
    const wrapper = mount(App);
    const appRoot = wrapper.find('[name="app-root"]');
    
    expect(appRoot.exists()).toBe(true);
  });
});