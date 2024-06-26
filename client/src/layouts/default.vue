<template>
  <section>
    <div class="notification-area">
      <Transition
        enter-active-class="slideY-fade-enter-from"
        leave-active-class="slideY-fade-leave-to"
      >
        <LazyCmNotification
          v-if="notification.isShow"
          :error="notification.isError"
          test-id="notification"
          @close="closeNotification"
        >
          <p>{{ notification.message ? $t(notification.message) : notification.customMessage }}</p>
        </LazyCmNotification>
      </Transition>
    </div>

    <slot />
  </section>
</template>

<script lang="ts" setup>
// #region Stores
const notificationStore = useNotificationStore()
// #endregion

// #region Computed
const notification = computed(() => {
  return notificationStore.notification
})
// #endregion

// #region Methods
const closeNotification = () => {
  notificationStore.updateNotification({
    message: '',
    isError: false,
    isShow: false
  })
}
// #endregion
</script>

<style lang="scss" scoped>
.notification-area {
  position: absolute;
  left: 0;
  right: 0;
  > * {
    margin: 5rem auto;
  }
}
</style>
