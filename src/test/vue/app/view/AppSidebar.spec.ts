import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import AppSidebar from '@/app/view/AppSidebar.vue';
import router from '@/app/router';

describe('AppSidebar', () => {
  it('should mount successfully', () => {
    const wrapper = mount(AppSidebar, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.exists()).toBe(true);
  });

  it('should render sidebar navigation items', () => {
    const wrapper = mount(AppSidebar, {
      global: {
        plugins: [router]
      }
    });
    
    const navList = wrapper.find('[name="sidebar-nav-list"]');
    expect(navList.exists()).toBe(true);
    
    const navItems = ['Главная', 'Проект', 'Архитектура', 'Анализ', 'Отчеты', 'MCP'];
    navItems.forEach(item => {
      expect(wrapper.text()).toContain(item);
    });
  });

  it('should highlight active navigation item', async () => {
    // Test for each route
    const routes = ['/', '/project', '/architecture', '/analysis', '/report', '/mcp'];
    
    for (const route of routes) {
      const wrapper = mount(AppSidebar, {
        global: {
          plugins: [router]
        }
      });
      
      await router.push(route);
      
      const activeItems = wrapper.find('.nav-item--active');
      expect(activeItems.exists()).toBe(true);
    }
  });

  it('should have sidebar info section', () => {
    const wrapper = mount(AppSidebar, {
      global: {
        plugins: [router]
      }
    });
    
    const infoSection = wrapper.find('[name="sidebar-info"]');
    expect(infoSection.exists()).toBe(true);
    
    expect(wrapper.text()).toContain('О приложении');
    expect(wrapper.text()).toContain('Spring Twin — MCP-агент для анализа Spring Boot проектов');
    expect(wrapper.text()).toContain('Версия');
    expect(wrapper.text()).toContain('1.0.0');
  });

  it('should have close button', () => {
    const wrapper = mount(AppSidebar, {
      global: {
        plugins: [router]
      }
    });
    
    const closeButton = wrapper.find('[name="sidebar-close"]');
    expect(closeButton.exists()).toBe(true);
    expect(closeButton.attributes('aria-label')).toBe('Закрыть меню');
  });

  it('should have correct semantic structure', () => {
    const wrapper = mount(AppSidebar, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.attributes('role')).toBe('complementary');
    expect(wrapper.attributes('aria-label')).toBe('Sidebar navigation');
    
    const nav = wrapper.find('nav');
    expect(nav.attributes('role')).toBe('navigation');
    expect(nav.attributes('aria-label')).toBe('Module navigation');
  });
});