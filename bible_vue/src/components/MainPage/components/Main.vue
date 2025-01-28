<template>
    <div id="app">
      <Header />
  
      <main class="main">
        <div class="main-image"></div>
        <div class="pagination">
          <span class="dot"></span>
          <span class="dot"></span>
          <span class="dot"></span>
          <span class="dot"></span>
        </div>
      </main>
    </div>
    <Footer />
  </template>
  
  <script>
  import Header from './Header.vue'
  import Footer from './Footer.vue';
export default {
  name: 'Main',
  components: {
    Header,  
    Footer
  }
}
</script>
  <style>
  /* Reset */
  body, ul, li {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  /* Main Content */
  .main {
    margin-top: 16px;
    text-align: center;
  }
  
  .main-image {
    width: 100%;
    height: 400px;
    background-color: #f0f0f0;
    border-radius: 8px;
  }
  
  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 16px;
    gap: 8px;
  }
  
  .dot {
    width: 12px;
    height: 12px;
    background-color: #ccc;
    border-radius: 50%;
    cursor: pointer;
  }
  
  .dot:hover {
    background-color: #333;
  }
  </style>
  