import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import AppLayout from '@/app/view/AppLayout.vue';
import router from '@/app/router';

describe('AppLayout', () => {
  it('should mount successfully', () => {
    const wrapper = mount(AppLayout, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.exists()).toBe(true);
  });

  it('should render all layout components', () => {
    const wrapper = mount(AppLayout, {
      global: {
        plugins: [router]
      }
    });
    
    // Check if all main layout components are rendered
    const header = wrapper.find('[name="app-header"]');
    const sidebar = wrapper.find('[name="app-sidebar"]');
    const main = wrapper.find('[name="main-content"]');
    const footer = wrapper.find('[name="app-footer"]');
    
    expect(header.exists()).toBe(true);
    expect(sidebar.exists()).toBe(true);
    expect(main.exists()).toBe(true);
    expect(footer.exists()).toBe(true);
  });

  it('should have correct semantic structure', () => {
    const wrapper = mount(AppLayout, {
      global: {
        plugins: [router]
      }
    });
    
    // Verify semantic HTML tags are used
    expect(wrapper.html()).toContain('<header');
    expect(wrapper.html()).toContain('<nav');
    expect(wrapper.html()).toContain('<main');
    expect(wrapper.html()).toContain('<footer');
    expect(wrapper.html()).toContain('<aside');
  });

  it('should have main content area', () => {
    const wrapper = mount(AppLayout, {
      global: {
        plugins: [router]
      }
    });
    
    const mainContent = wrapper.find('.app-layout__content');
    expect(mainContent.exists()).toBe(true);
    expect(mainContent.attributes('role')).toBe('main');
  });

  it('should have responsive layout classes', () => {
    const wrapper = mount(AppLayout, {
      global: {
        plugins: [router]
      }
    });
    
    // Check that layout structure exists
    expect(wrapper.find('.app-layout').exists()).toBe(true);
    expect(wrapper.find('.app-layout__body').exists()).toBe(true);
    expect(wrapper.find('.app-layout__content').exists()).toBe(true);
  });
});