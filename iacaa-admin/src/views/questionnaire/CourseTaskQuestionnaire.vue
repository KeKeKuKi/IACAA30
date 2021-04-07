<template>
  <div :style="backgroundDiv">
    <div style="width: 90%;margin-left: 5%;text-align: left">
      <br>
      <div style="color: #001742;font-size: 22px">欢迎来到攀枝花学院学生毕业要求达成评价及分析系统</div>
      <br>
      <div style="color: #070153;font-size: 18px">下面您将作为本届毕业生为统计本届学生毕业要求达成度做出重要贡献，您可能需要花费三分钟完成此调查问卷，请务必认真填写！</div>
      <br>
      <div style="color: #5e5e5e;font-size: 16px">您可以完成问卷后继续作答，我们将自动为您生成新的提问！</div>
      <hr>
      <br>
      <br>
      <span>
    <el-form :inline="true" :model="questionnaireForm" class="demo-form-inline" style="color:#343434;">
      <el-form-item label="您的学号:">
        <el-input v-model="questionnaireForm.stuNo" placeholder="请输入您的学号" clearable/>
      </el-form-item>
    </el-form>
    </span>
      <hr>
      <span v-for="(item,i) in questionnaireForm.courseTaskEvaluations">
    <div style="color: #282828">
      问题{{ i + 1 }}:
    </div>
    <div style="color: #282828">
      您对课程<span style="font-size: 16px;color: #05637f">{{ item.courseTaskVo.course.name }}</span>
      的课程目标:<span style="font-size: 16px;color: #0c8e42">{{ item.courseTaskVo.describes }}</span>的评价为
    </div>
    <el-rate
      v-model="item.score"
      show-text
      @change="changeprogress">
    </el-rate>
    <hr>
    <br>
    </span>
      <br>
      <el-button type="primary" v-if="this.progress === 100" @click="submitForm" style="margin-left: 80%">提交问卷
      </el-button>
      <br>
      <br>
      <el-progress :text-inside="true" :stroke-width="24" :percentage="this.progress" status="success"></el-progress>
    </div>
  </div>
</template>

<script>
import {requestByClient, supplierConsumer} from "@/utils/HttpUtils";

export default {
  name: "CourseTaskQuestionnaire",
  data() {
    return {
      backgroundDiv: {
        backgroundImage: 'url(' + require('@/assets/images/bac.jpg') + ')',
        backgroundRepeat: 'no-repeat',
        backgroundSize: '100% 100%',
        width: '100%',
        height: '100%',
      },
      value: '',
      progress: 0,
      questionnaireForm: {
        stuNo: '',
        courseTaskEvaluations: []
      }
    }
  },
  methods: {
    submitForm() {
      requestByClient(supplierConsumer, 'POST', 'stuEvaluation/saveAll',
        {
          stuEvaluations: this.questionnaireForm.courseTaskEvaluations.map(i => {
            return {
              courseTask: i.courseTaskVo.id,
              stuNo: this.questionnaireForm.stuNo,
              score: i.score
            }
          })
        }
        , res => {
          if (res.data.succ) {
            this.$message({
              message: '感谢您的支持，祝您学习顺利',
              type: 'success'
            });
          }
          this.getList()
        })
    },
    changeprogress() {
      if (this.progress < 100) {
        this.progress = this.progress + 100 / this.questionnaireForm.courseTaskEvaluations.length
      }
    },
    getList() {
      requestByClient(supplierConsumer, 'POST', 'stuEvaluation/getQuestions', {}, res => {
        if (res.data.succ) {
          this.questionnaireForm.courseTaskEvaluations = res.data.data
        }
      })
    },
  },
  mounted() {
    this.getList()
  }
}
</script>

<style scoped>
.el-progress {

}
</style>
