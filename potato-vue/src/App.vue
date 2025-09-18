<template>
  <div id="app">
    <component>
      <router-view />
    </component>
  </div>
</template>

<script setup>



</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  //display: flex;
  //align-items: center;
  //justify-content: center;
  //margin-top: 60px;
}
body {
  /*cursor: crosshair;*/ /* 全局十字光标 */
  cursor: url("@/assets/mouse_cursor.png") 0 0, auto !important;
}
*:hover {
  cursor: url("@/assets/mouse_hover.png") auto !important;
}
</style>
