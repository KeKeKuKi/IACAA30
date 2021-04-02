<template>
  <div>
    <div class="historyLabel">
      <el-select v-model="serchForm.year" placeholder="选择事件" filterable clearable @change="getList()">
        <el-option label="2021" value="2021" />
        <el-option label="2020" value="2020" />
      </el-select>
      <span style="float: right;margin-right: 180px">
        <el-button type="primary" @click="refreshData">刷新数据</el-button>
      </span>
      <div id="historyData" class="historyCanvas" />
    </div>
  </div>
</template>

<script>
import echarts from 'echarts'
import { requestByClient } from '@/utils/HttpUtils'
import { supplierConsumer } from '@/utils/HttpUtils'
import { Loading } from 'element-ui'
export default {
  name: "CourseAnalysis",

  data() {
    return {
      serchForm: {
        year: new Date().getFullYear()
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    refreshData(){
      const loadingInstance = Loading.service({
        background: 'rgba(0, 0, 0, 0.7)',
        text: '加载中，请稍后。。。',
        target: 'document.body',
        body: true
      })
      requestByClient(supplierConsumer, 'POST', 'gradRequirement/summaryAll', {
      },res => {
        if (res.data.succ) {
          this.$message({
            message: '数据已刷新',
            type: 'success'
          })
          this.getList()
          // 关闭加载动画
          this.$nextTick(() => {
            loadingInstance.close()
          })
        }
        this.loading = false
      })
    },
    getList() {
      requestByClient(supplierConsumer, 'POST', 'courseTask/voList', {
        year: this.serchForm.year
      },res => {
        if (res.data.succ) {
          let data = res.data.data
          let courseTasksName = data.map(i => {return i.course.name + ':' + i.describes})
          let sysScores = data.map(i => {
            return i.sysGrade.toFixed(2)*100
          })
          let stuScores = data.map(i => {
            return i.stuGrade.toFixed(2)*100
          })
          this.setChartData(courseTasksName, sysScores, stuScores)
        }
        this.loading = false
      })
    },
    setChartData(names, sysScores, stuScores) {
      const chartDom = document.getElementById('historyData')
      const myChart = echarts.init(chartDom)
      let option
      option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        dataZoom: [
          {
            id: 'dataZoomX',
            type: 'slider',
            xAxisIndex: [0],
            filterMode: 'filter'
          }
        ],
        toolbox: {
          show: true,
          feature: {
            magicType: { show: true, type: ['line', 'bar'] },
            restore: { show: true },
            saveAsImage: { show: true }
          }
        },
        calculable: true,
        title: {
          text: '课程目标成绩对比分析',
          subtext: ''
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            interval:0,
            rotate:50
          }
        },
        yAxis: {
          type: 'value',
          max: 100
        },
        series: [{
          name: '系统成绩',
          data: sysScores,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#317ed5'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              { type: 'max', name: '最大值' }
            ]
          },
          markLine: {
            data: [
              { type: 'average', name: '平均值' }
            ]
          }
        }, {
          name: '学生评价',
          data: stuScores,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#1a30a0'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              { type: 'max', name: '最大值' }
            ]
          },
          markLine: {
            data: [
              { type: 'average', name: '平均值' }
            ]
          }
        }
        ]
      }
      option && myChart.setOption(option)
    },
  }
}
</script>

<style scoped>
.historyLabel{
  padding: 20px;
  margin: 20px;
  width: 97%;
  height: 830px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .05)
}
.historyCanvas{
  width: 100%;
  height: 700px;
  padding: 0;
  margin: 0;
}
</style>
