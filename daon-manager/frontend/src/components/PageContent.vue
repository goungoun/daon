<template>
  <div class="page-content">
    <md-whiteframe md-tag="md-toolbar" md-elevation="1" class="main-header">
      <md-button class="md-icon-button nav-trigger" @click.native="toggleSidenav">
        <md-icon>menu</md-icon>
      </md-button>

      <div class="md-title">{{ pageTitle }}</div>

      <!--<md-button href="https://github.com/rasoio/daon" target="_blank" rel="noopener" class="md-icon-button github">-->
        <!--<img src="../assets/icon-github.svg" alt="Material Design">-->
      <!--</md-button>-->
      <md-button @click.native="toggleRightSidenav" class="md-icon-button github">
        <md-icon>build</md-icon>
      </md-button>
    </md-whiteframe>

    <slot></slot>
  </div>
</template>

<style lang="scss" scoped>
  .page-content {
    min-height: 100%;
    max-height: 100%;
    flex: 1;
    display: flex;
    flex-flow: column
  }
  .main-header {
    z-index: 2;
    color: #fff !important;
  }
  .nav-trigger {
  @media (min-width: 1281px) {
    display: none;
  }
  }
  .md-title {
    flex: 1;
  @media (min-width: 1281px) {
    margin-left: 8px;
  }
  }
  .github {
  @media (max-width: 480px) {
    display: none;
  }
  }
</style>

<script>
  export default {
    props: {
      pageTitle: String
    },
    methods: {
      toggleSidenav() {
        this.$root.toggleSidenav();
      },
      toggleRightSidenav() {
        this.$root.toggleRightSidenav()
      }
    },
    mounted() {
      document.title = this.pageTitle + ' - Vue Material';
    }
  };
</script>
