<template>
    <footer class="footer">
      <div class="footer-content">
        <p>© 2025 Your Website. All rights reserved.</p>
        <div class="footer-links">
          <a href="#">Privacy Policy</a>
          <a href="#">Terms of Service</a>
          <a href="#">Contact Us</a>
        </div>
      </div>
    </footer>
  </template>
  
  <script>
  export default {
    name: 'Footer'
  }
  </script>
    <style scoped>
    .footer {
      background-color: #eee;
      color: #333;
      padding: 20px 0;
      text-align: center;
    }
    
    .footer-content {
      max-width: 1280px;
      margin: 0 auto;
    }
    
    .footer-links {
      margin-top: 10px;
    }
    
    .footer-links a {
      color: #333;
      text-decoration: none;
      margin: 0 10px;
      font-size: 14px;
    }
    
    .footer-links a:hover {
      text-decoration: underline;
    }
    </style>