<template>
    <h1>알람 전송</h1>
    <SendSms/>
    <br>
    <SendWebSocketAlarm/>
</template>

<script>
import SendSms from "..//Alarm/components/SendSms.vue";
import SendWebSocketAlarm from "../Alarm/components/SendWebSocketAlarm.vue";

export default{
    data() {
    },
    components: {
        SendSms,
        SendWebSocketAlarm
    },
    methods:{

    }
}

</script>