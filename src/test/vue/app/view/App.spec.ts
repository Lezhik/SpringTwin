import { describe, it, expect, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import App from '@/App.vue';
import router from '@/app/router';

describe('App', () => {
  it('should mount successfully', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.exists()).toBe(true);
  });

  it('should have correct structure with all layout components', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [router]
      }
    });
    
    // Verify the AppLayout component is rendered
    const layout = wrapper.find('[name="app-layout"]');
    expect(layout.exists()).toBe(true);
    
    // Verify header exists
    const header = wrapper.find('[name="app-header"]');
    expect(header.exists()).toBe(true);
    
    // Verify sidebar exists
    const sidebar = wrapper.find('[name="app-sidebar"]');
    expect(sidebar.exists()).toBe(true);
    
    // Verify main content exists
    const mainContent = wrapper.find('[name="main-content"]');
    expect(mainContent.exists()).toBe(true);
    
    // Verify footer exists
    const footer = wrapper.find('[name="app-footer"]');
    expect(footer.exists()).toBe(true);
  });

  it('should display correct app title in header', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [router]
      }
    });
    
    const header = wrapper.find('[name="app-header"]');
    expect(header.text()).toContain('Spring Twin');
  });

  it('should have sidebar navigation', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [router]
      }
    });
    
    const sidebar = wrapper.find('[name="app-sidebar"]');
    const navList = sidebar.find('[name="sidebar-nav-list"]');
    expect(navList.exists()).toBe(true);
    
    // Check that all main navigation items exist
    const navItems = ['Главная', 'Проект', 'Архитектура', 'Анализ', 'Отчеты', 'MCP'];
    navItems.forEach(item => {
      expect(wrapper.text()).toContain(item);
    });
  });

  it('should render footer with copyright information', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [router]
      }
    });
    
    const footer = wrapper.find('[name="app-footer"]');
    expect(footer.text()).toContain('©');
    expect(footer.text()).toContain('Spring Twin');
  });

  it('should have proper semantic structure', () => {
    const wrapper = mount(App, {
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
});