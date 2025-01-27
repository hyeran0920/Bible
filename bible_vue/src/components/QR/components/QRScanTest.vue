
<template>
    <div class="qr-scanner">
        <h1>QR Code Scanner</h1>
        <div id="qr-reader"></div>
        <div v-if="scannedData" class="result">
        <h2>Scanned QR Data:</h2>
        <p>{{ scannedData }}</p>
        </div>
        <div v-else class="placeholder">
        <p>Scan a QR code to see the result here.</p>
        </div>
    </div>
</template>


<script>
    import { Html5QrcodeScanner } from "html5-qrcode";

    export default {
        data() {
            return {
            scannedData: null, // QR code data
            };
        },
        mounted() {
            const scanner = new Html5QrcodeScanner("qr-reader", {
            fps: 10,
            qrbox: 250,
            });
            scanner.render(
            (decodedText) => {
                this.scannedData = decodedText;
            },
            (error) => {
                console.error("Scan failed:", error);
            }
            );
        },
    };
</script>
  
