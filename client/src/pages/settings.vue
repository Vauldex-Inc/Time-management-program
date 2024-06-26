<template>
  <section class="settings">
    <header>
      <CmPaneHeader
        :title="title"
        icon="settings"
        :breadcrumb="breadcrumb"
      />
    </header>

    <NuxtPage @before-mount:page="setPageTitle" />
  </section>
</template>

<script lang="ts" setup>
definePageMeta({
  layout: 'settings',
  middleware: 'authentication'
})

// #region State
const title = ref<string>()
const breadcrumb = ref([] as unknown[])
// #endregion

// #region Methods
const setPageTitle = (page: { title: string, breadcrumb?: unknown[] }) => {
  title.value = page.title
  breadcrumb.value = page.breadcrumb ?? []
}
// #endregion
</script>

<style lang="scss" scoped>
.settings {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}
</style>
