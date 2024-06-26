<template>
  <section>
    <header>
      <CmPaneHeader
        icon="clock"
        :title="$t('tracking_box.title')"
      >
        <template #selection>
          <CmPaneHeaderSelection
            :placeholder="viewLabels[currentView]"
            :items="viewOptions"
            @select="onSelectView"
          />
        </template>
      </CmPaneHeader>
    </header>

    <NuxtPage />
  </section>
</template>

<script lang="ts" setup>
import { PathNames } from '~/types/static'

definePageMeta({
  layout: 'base',
  middleware: 'authentication'
})

// #region Types
const viewList = [
  'list-view',
  'timeline-view'
] as const

type View = typeof viewList[number]
// #endregion

// #region Composables
const { t } = useI18n()
const route = useRoute()
// #endregion

// #region Constants
const viewLabels: Record<View, string> = {
  'list-view': t('page.timestamps_list'),
  'timeline-view': t('page.timestamps_timeline')
}

const viewOptions = viewList.map((k) => {
  return {
    label: viewLabels[k],
    id: k
  }
})
// #endregion

// #region Computed
const currentView = computed(() => {
  const path = route.fullPath.split('?')[0]
  const fullPathSplit = path.split('/')
  const viewStr = fullPathSplit[fullPathSplit.length - 1]

  return isValidView(viewStr)
    ? viewStr
    : viewList[0]
})
// #endregion

// #region Methods
const isValidView = (viewStr: string): viewStr is View => {
  return viewList.includes(viewStr as View)
}

const onSelectView = (view: string) => {
  if (isValidView(view)) {
    switch (view) {
      case 'list-view': navigateToLocale(PathNames.Timestamps)
        break
      case 'timeline-view': navigateToLocale(PathNames.Timeline)
        break
    }
  }
}
// #endregion
</script>

<style lang="scss" scoped>
section {
  height: 100%;
}
</style>
