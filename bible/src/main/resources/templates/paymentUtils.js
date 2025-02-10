const axios = window.axios;

const BASE_URL=`http://localhost:8080/api/`;

//Fetch Data///////////////////////////////////////////////////////////////////////////////////////////////////
function fetchOrders(orderHistoryId) {
     return new Promise(async (resolve, reject) => {
         try {

             const response = await axios.get(BASE_URL+`orders/history/${orderHistoryId}`);
             const orders = response.data;

             resolve(orders);
         } catch (error) {
             console.error("`fetchOrders()` 실행 중 오류 발생:", error);
             reject(error);
         }
     });
 }
 
 
function fetchOrderHistory(orderHistoryId) {
     return new Promise(async (resolve, reject) => {
     	
     	if (!orderHistoryId) {
             console.error("❌ orderHistoryId가 없습니다.");
             reject(new Error("orderHistoryId가 없습니다."));
             return;
         }
     	
         try {
             const response = await axios.get(BASE_URL+`orderhistory/${orderHistoryId}`);
             const orderHistory = response.data;
             
             //document.getElementById("orderHistoryId").textContent = orderHistory.orderHistoryId;
             resolve(orderHistory);
             
         } catch (error) {
             console.error("`fetchOrderHistory()` 실행 중 오류 발생:", error);
             reject(error);
         }
     });
 }

 
function fetchBookPrice(bookId){
	return new Promise(async (resolve, reject) => {
     	
     	if (!bookId) {
             console.error("❌ bookId가 없습니다.");
             reject(new Error("bookId가 없습니다."));
             return;
         }
     	
         try {
             const response = await axios.get(BASE_URL+`books/${bookId}`);
             const bookPrice = response.data.bookPrice;

             resolve(bookPrice);
             
         } catch (error) {
             console.error("`fetchBooks()` 실행 중 오류 발생:", error);
             reject(error);
         }
     });
 }
 

function fetchMemInfo(memId){
	return new Promise(async (resolve, reject) => {
     	
     	if (!memId) {
             console.error("❌ memId가 없습니다.");
             reject(new Error("memId가 없습니다."));
             return;
         }
     	
         try {
			console.log("trying to get member info");
             const response = await axios.get(BASE_URL+`members/token`);
             const memInfo = response.data;

             resolve(memInfo);
             
         } catch (error) {
             console.error("`fetchMemInfo()` 실행 중 오류 발생:", error);
             reject(error);
         }
     });
 }

 




 //Check total price////////////////////////////////////////////////////////////////////////////////////////
async function validateTotalPrice(orders, orderHistory) {
     let totalPriceCalculate = 0;

     try {
         // 각 책의 가격을 가져오는 비동기 요청을 병렬로 처리
         const bookPrices = await Promise.all(
             orders.map(async (order) => {
                 const bookPrice = await fetchBookPrice(order.bookId);
                 return bookPrice * order.bookCount;
             })
         );

         
         // 총 가격 계산
         totalPriceCalculate = bookPrices.reduce((sum, price) => sum + price, 0);

         
         // 총 가격 검증
         if (totalPriceCalculate !== orderHistory.orderHistoryTotalPrice) {
             throw new Error("Order history total price doesn't match!");
         }

         console.log("Total price validation successful!");
		 return totalPriceCalculate;
         
     } catch (error) {
         console.error("Error in price validation:", error.message);
         throw error;
		 return error;
     }
 }

 
 
   
 //Random number generator
function generateRandomString() {
     return window.btoa(Math.random()).slice(0, 20);
 }
 
 
 
 export {
     fetchOrders,
     fetchOrderHistory,
     fetchBookPrice,
     fetchMemInfo,
     validateTotalPrice,
     generateRandomString
 };
