<template>
  <li 
    class="nav-item" 
    :class="{ 'nav-item--active': isActive, 'nav-item--has-children': hasChildren }"
    role="menuitem"
    name="nav-item"
  >
    <router-link
      v-if="!hasChildren"
      :to="item.path"
      class="nav-item__link"
      :aria-current="isActive ? 'page' : false"
    >
      <span class="nav-item__icon" name="nav-icon">{{ item.icon }}</span>
      <span class="nav-item__text" name="nav-text">{{ item.name }}</span>
    </router-link>

    <div v-else class="nav-item__dropdown">
      <button 
        class="nav-item__link nav-item__link--dropdown"
        @click="toggleDropdown"
        :aria-expanded="isDropdownOpen"
        :aria-haspopup="true"
      >
        <span class="nav-item__icon" name="nav-icon">{{ item.icon }}</span>
        <span class="nav-item__text" name="nav-text">{{ item.name }}</span>
        <span class="nav-item__arrow" name="nav-arrow">
          {{ isDropdownOpen ? '▼' : '▶' }}
        </span>
      </button>

      <ul 
        class="nav-item__children" 
        :class="{ 'nav-item__children--open': isDropdownOpen }"
        role="menu"
        name="nav-children"
      >
        <NavItem
          v-for="child in item.children"
          :key="child.id"
          :item="child"
          :level="(level || 0) + 1"
        />
      </ul>
    </div>
  </li>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import type { NavigationItem } from '../api/navigation';

/**
 * Navigation item data.
 */
const props = withDefaults(defineProps<{
  item: NavigationItem;
  level?: number;
}>(), {
  level: 0
});

const route = useRoute();
const isDropdownOpen = ref(false);

/**
 * Checks if the current route matches the navigation item.
 */
const isActive = computed(() => {
  if (props.item.path === '/') {
    return route.path === '/';
  }
  return route.path.startsWith(props.item.path);
});

/**
 * Checks if the navigation item has children.
 */
const hasChildren = computed(() => {
  return props.item.children && props.item.children.length > 0;
});

/**
 * Toggles dropdown menu visibility.
 */
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};
</script>

<style scoped>
.nav-item {
  display: block;
  margin: 0;
  padding: 0;
  list-style: none;
}

.nav-item__link {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.75rem 1rem;
  color: #333;
  text-decoration: none;
  background-color: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 4px;
}

.nav-item__link:hover {
  background-color: #f5f5f5;
}

.nav-item--active .nav-item__link {
  background-color: #4CAF50;
  color: white;
}

.nav-item__icon {
  margin-right: 0.75rem;
  font-size: 1.1rem;
  line-height: 1;
}

.nav-item__text {
  flex: 1;
  font-size: 0.95rem;
  font-weight: 500;
}

.nav-item__arrow {
  margin-left: 0.5rem;
  font-size: 0.8rem;
  transition: transform 0.2s ease;
}

.nav-item--has-children .nav-item__link {
  justify-content: space-between;
}

.nav-item__children {
  max-height: 0;
  overflow: hidden;
  margin: 0;
  padding: 0 0 0 1.5rem;
  list-style: none;
  transition: max-height 0.3s ease;
  background-color: #fafafa;
}

.nav-item__children--open {
  max-height: 500px;
}

.nav-item__children .nav-item__link {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .nav-item__link {
    padding: 0.6rem 0.8rem;
  }

  .nav-item__text {
    font-size: 0.9rem;
  }
}
</style>