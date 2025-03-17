<template>
  <el-input
      v-model="query"
      placeholder="请输入视频标题"
      @input="handleInput"
  />
</template>

<script>
import {ref} from 'vue';

export default {
  props: {
    modelValue: String
  },
  setup(props, {emit}) {
    const query = ref(props.modelValue);

    const handleInput = () => {
      emit('search', query.value);
    };

    return {query, handleInput};
  },
};
</script>
  