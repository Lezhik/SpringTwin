import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import AppFooter from '@/app/view/AppFooter.vue';
import router from '@/app/router';

describe('AppFooter', () => {
  it('should mount successfully', () => {
    const wrapper = mount(AppFooter, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.exists()).toBe(true);
  });

  it('should display app name and description', () => {
    const wrapper = mount(AppFooter, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.text()).toContain('Spring Twin');
    expect(wrapper.text()).toContain('MCP-агент для анализа Spring Boot проектов');
  });

  it('should render resources section', () => {
    const wrapper = mount(AppFooter, {
      global: {
        plugins: [router]
      }
    });
    
    const resources = ['Документация', 'GitHub', 'Issues'];
    resources.forEach(resource => {
      expect(wrapper.text()).toContain(resource);
    });
  });

  it('should render contacts section', () => {
    const wrapper = mount(AppFooter, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.text()).toContain('support@springtwin.com');
    expect(wrapper.text()).toContain('springtwin.com');
  });

  it('should render copyright information', () => {
    const wrapper = mount(AppFooter, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.text()).toContain('©');
    expect(wrapper.text()).toContain('2024');
    expect(wrapper.text()).toContain('Все права защищены');
  });

  it('should have correct semantic structure', () => {
    const wrapper = mount(AppFooter, {
      global: {
        plugins: [router]
      }
    });
    
    expect(wrapper.attributes('role')).toBe('contentinfo');
  });

  it('should have working links', () => {
    const wrapper = mount(AppFooter, {
      global: {
        plugins: [router]
      }
    });
    
    // Check email link
    const emailLink = wrapper.find('a[href^="mailto:"]');
    expect(emailLink.exists()).toBe(true);
    expect(emailLink.attributes('href')).toBe('mailto:support@springtwin.com');
    
    // Check website link
    const websiteLink = wrapper.find('a[href="https://springtwin.com"]');
    expect(websiteLink.exists()).toBe(true);
    expect(websiteLink.attributes('target')).toBe('_blank');
    expect(websiteLink.attributes('rel')).toBe('noopener noreferrer');
  });
});