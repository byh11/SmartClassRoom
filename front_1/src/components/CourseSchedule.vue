<template>
  <div class="course-schedule">
    <el-calendar v-model="currentDate">
      <template #dateCell="{ data }">
        <div class="calendar-cell">
          <p>{{ data.day.split('-').slice(1).join('-') }}</p>
          <div class="course-list">
            <div
                v-for="course in getCourses(data.day)"
                :key="course.id"
                :class="course.status"
                class="course-item"
                @click="handleCourseClick(course)"
            >
              <p class="course-name">{{ course.name }}</p>
              <p class="course-time">{{ course.time }}</p>
            </div>
          </div>
        </div>
      </template>
    </el-calendar>
  </div>
</template>

<script setup>
import {ref} from 'vue'

const currentDate = ref('')

const getCourses = (day) => {
  // Implementation of getCourses function
}

const handleCourseClick = (course) => {
  // Implementation of handleCourseClick function
}
</script>

<style scoped>
/* Add your styles here */
</style> 