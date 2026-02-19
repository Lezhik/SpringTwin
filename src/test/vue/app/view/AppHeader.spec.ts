import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import AppHeader from '@/app/view/AppHeader.vue';
import router from '@/app/router';

describe('AppHeader', () => {
  it('should mount successfully', () => {
    const wrapper = mount(AppHeader, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.exists()).toBe(true);
  });

  it('should display app title', () => {
    const wrapper = mount(AppHeader, {
      global: {
        plugins: [router]
      }
    });
    
    const title = wrapper.find('[name="logo-text"]');
    expect(title.exists()).toBe(true);
    expect(title.text()).toContain('Spring Twin');
  });

  it('should render header navigation items', () => {
    const wrapper = mount(AppHeader, {
      global: {
        plugins: [router]
      }
    });
    
    const navLinks = [
      { name: 'Главная', path: 'home' },
      { name: 'Проект', path: 'project' },
      { name: 'Архитектура', path: 'architecture' },
      { name: 'Анализ', path: 'analysis' },
      { name: 'Отчеты', path: 'report' },
      { name: 'MCP', path: 'mcp' }
    ];
    
    navLinks.forEach(link => {
      const navItem = wrapper.find(`[name="nav-link-${link.path}"]`);
      expect(navItem.exists()).toBe(true);
      expect(navItem.text()).toContain(link.name);
    });
  });

  it('should highlight active navigation item', async () => {
    // Test for each route
    const routes = ['/', '/project', '/architecture', '/analysis', '/report', '/mcp'];
    
    for (const route of routes) {
      const wrapper = mount(AppHeader, {
        global: {
          plugins: [router]
        }
      });
      
      await router.push(route);
      
      const activeLink = wrapper.find('.app-header__nav-link--active');
      expect(activeLink.exists()).toBe(true);
      expect(activeLink.attributes('href')).toContain(route);
    }
  });

  it('should have menu toggle button', () => {
    const wrapper = mount(AppHeader, {
      global: {
        plugins: [router]
      }
    });
    
    const menuButton = wrapper.find('[name="action-btn"]');
    expect(menuButton.exists()).toBe(true);
    expect(menuButton.text()).toContain('Меню');
  });

  it('should have correct semantic structure', () => {
    const wrapper = mount(AppHeader, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.attributes('role')).toBe('banner');
    expect(wrapper.find('nav').attributes('role')).toBe('navigation');
    expect(wrapper.find('nav').attributes('aria-label')).toBe('Primary navigation');
  });
});