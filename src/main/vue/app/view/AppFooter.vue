<template>
  <footer class="app-footer" name="app-footer" role="contentinfo">
    <div class="app-footer__container">
      <div class="app-footer__content">
        <div class="app-footer__section">
          <h3 class="app-footer__title" name="footer-title">Spring Twin</h3>
          <p class="app-footer__text" name="footer-text">
            MCP-агент для анализа Spring Boot проектов
          </p>
        </div>

        <div class="app-footer__section">
          <h3 class="app-footer__title" name="footer-title">Ресурсы</h3>
          <ul class="app-footer__list" name="footer-list">
            <li class="app-footer__list-item" name="list-item">
              <a 
                href="#" 
                class="app-footer__link"
                name="footer-link"
              >
                Документация
              </a>
            </li>
            <li class="app-footer__list-item" name="list-item">
              <a 
                href="#" 
                class="app-footer__link"
                name="footer-link"
              >
                GitHub
              </a>
            </li>
            <li class="app-footer__list-item" name="list-item">
              <a 
                href="#" 
                class="app-footer__link"
                name="footer-link"
              >
                Issues
              </a>
            </li>
          </ul>
        </div>

        <div class="app-footer__section">
          <h3 class="app-footer__title" name="footer-title">Контакты</h3>
          <ul class="app-footer__list" name="footer-list">
            <li class="app-footer__list-item" name="list-item">
              <a 
                href="mailto:support@springtwin.com" 
                class="app-footer__link"
                name="footer-link"
              >
                support@springtwin.com
              </a>
            </li>
            <li class="app-footer__list-item" name="list-item">
              <a 
                href="https://springtwin.com" 
                class="app-footer__link"
                name="footer-link"
                target="_blank"
                rel="noopener noreferrer"
              >
                springtwin.com
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div class="app-footer__bottom">
        <p class="app-footer__copyright" name="copyright">
          © 2024 Spring Twin. Все права защищены.
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
// Footer component logic
</script>

<style scoped>
.app-footer {
  background-color: #2c3e50;
  color: white;
  padding: 2rem 0 1rem;
  margin-top: auto;
}

.app-footer__container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

.app-footer__content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.app-footer__section {
  display: flex;
  flex-direction: column;
}

.app-footer__title {
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #4CAF50;
}

.app-footer__text {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.5;
  color: #bdc3c7;
}

.app-footer__list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.app-footer__list-item {
  margin: 0;
  margin-bottom: 0.75rem;
}

.app-footer__list-item:last-child {
  margin-bottom: 0;
}

.app-footer__link {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s ease;
}

.app-footer__link:hover {
  color: #4CAF50;
}

.app-footer__bottom {
  border-top: 1px solid #34495e;
  padding-top: 1rem;
  text-align: center;
}

.app-footer__copyright {
  margin: 0;
  font-size: 0.85rem;
  color: #95a5a6;
}

/* Responsive design */
@media (max-width: 768px) {
  .app-footer {
    padding: 1.5rem 0 1rem;
  }

  .app-footer__container {
    padding: 0 1rem;
  }

  .app-footer__content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .app-footer__title {
    font-size: 1rem;
  }

  .app-footer__text,
  .app-footer__link {
    font-size: 0.85rem;
  }
}
</style>