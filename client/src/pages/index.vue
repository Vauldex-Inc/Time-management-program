<template>
  <section class="home">
    <ClientOnly>
      <CmColorBlurBackground />
    </ClientOnly>

    <header class="home__header">
      <CmHomeHeader />
    </header>

    <div class="home__content">
      <CmHomeHero />
      <CmHomeFeature id="features" />

      <div class="call-to-action">
        <CmCallToAction />
      </div>
    </div>

    <footer>
      <CmHomeFooter />
      <CmFooter
        :is-home-page="true"
        :with-extension="true"
      />
    </footer>
  </section>
</template>

<script lang="ts" setup>
definePageMeta({
  middleware: 'guest',
  colorMode: 'light'
})

useHead({
  link: [
    {
      rel: 'preconnect',
      href: 'https://fonts.googleapis.com'
    },
    {
      rel: 'preconnect',
      href: 'https://fonts.gstatic.com'
    },
    {
      rel: 'stylesheet',
      href: 'https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@100;200;300;400;500;600;700;800;900&display=swap'
    }
  ]
})
</script>

<style lang="scss" scoped>
.home {
  min-width: 100vw;

  &__header {
    width: 100%;
    top: 0;
    position: sticky;
    z-index: 4;
    & + .call-to-action {
      padding-top: 102px;
    }
  }

  &__content {
    overflow: hidden;
  }

  @media screen and (max-width: 980px) {
    &__header {
      position: fixed;
    }
    padding-top: 4.875rem;
  }
}

.call-to-action {
  padding: 2em;
  z-index: auto;
}
</style>
