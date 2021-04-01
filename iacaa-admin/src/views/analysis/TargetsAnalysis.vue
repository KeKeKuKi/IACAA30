<template>
  <div>
    <div class="historyLabel">
<!--      <el-cascader-->
<!--        v-model="serchForm.id"-->
<!--        :options="options"-->
<!--        :props="{ expandTrigger: 'hover' }"-->
<!--        @change="handleChange">-->
<!--      </el-cascader>-->
      <span style="float: right;margin-right: 180px">
        <el-button type="primary" @click="refreshData">刷新数据</el-button>
      </span>
      <div id="historyData" class="historyCanvas"/>
    </div>
    <el-dialog
      :title="targetChartForm.title"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="90%"
      @open="open"
      center
    >
      <span id="targetPie" class="targetPie"/>
      <span id="targetBar" class="targetBar"/>
    </el-dialog>

  </div>
</template>

<script>
import echarts from 'echarts'
import {requestByClient} from '@/utils/HttpUtils'
import {supplierConsumer} from '@/utils/HttpUtils'
import {Loading} from 'element-ui'

export default {
  name: "TargetsAnalysis",
  data() {
    return {
      serchForm: {
        year: new Date().getFullYear(),
        id: '',
      },
      options: [],
      dialogVisible: false,
      targetChartForm: {
        title: '',
        score: '',
        courseTargets: [],
        courseTasks: []
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    setChartData(targetsName, series) {
      let vue = this
      const chartDom = document.getElementById('historyData')
      const myChart = echarts.init(chartDom)
      let option
      let colors = [
        '#199237',
        '#196292',
        '#c11a9d',
        '#e5da14',
        '#b89220',
        '#1c977a',
        '#9a5a2b',
      ]
      let myseries = {
        name: "系统统计成绩",
        data: series.map(item => {
          return item.value
        }),
        type: 'bar',
        itemStyle: {
          normal: {
            color: '#196292'
          }
        },
        showBackground: true,
        backgroundStyle: {
          color: 'rgba(180,180,180,0.2)'
        }
      }
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
        calculable: true,
        title: {
          text: '毕业要求成绩统计',
          subtext: ''
        },
        xAxis: {
          type: 'category',
          data: targetsName,
          axisLabel: {
            interval: 0,
            rotate: 40
          }
        },
        yAxis: {
          type: 'value',
          max: 100
        },
        series: myseries
      }
      option && myChart.setOption(option)
      //点击事件
      myChart.on('click', function (params) {
        let po = params.name.indexOf(':')
        vue.handleChange(parseInt(params.name.substring(0,po)))
      });
    },
    open() {
      this.$nextTick(() => {
        this.setTargetChart()
        this.setTargetChartBar()
      })
    },
    handleChange(value) {
      requestByClient(supplierConsumer, 'POST', 'target/getOne', {
        id: value
      }, res => {
        if (res.data.succ) {
          if (res.data.data.sysGrade > 0) {
            this.targetChartForm.title = res.data.data.discribe + '(' + (res.data.data.sysGrade * 100).toFixed(2) + ')'
            this.targetChartForm.score = res.data.data.sysGrade
            requestByClient(supplierConsumer, 'POST', 'courseTarget/voList', {
              targetId: value
            }, res => {
              if (res.data.succ) {
                this.targetChartForm.courseTargets = res.data.data
                requestByClient(supplierConsumer, 'POST', 'courseTask/voList', {
                  targetId: value,
                }, res => {
                  if (res.data.succ) {
                    this.targetChartForm.courseTasks = res.data.data
                    this.dialogVisible = true
                  }
                })
              }
            })
          } else {
            this.$message({
              message: '该指标点暂无成绩',
              type: 'warning'
            })
            return false
          }
        }
      })

    },
    setTargetChartBar() {
      let chartDom = document.getElementById('targetBar');
      const myChart = echarts.init(chartDom)
      let option
      let courseTasks = this.targetChartForm.courseTasks
      let colors = [
        '#199237',
        '#196292',
        '#c11a9d',
        '#e5da14',
        '#b89220',
        '#1c977a',
        '#9a5a2b',
      ]
      let tasksName = courseTasks.map(i => {
        return i.describes + '(' + i.course.name + ')'
      })

      let tasksScores = courseTasks.map(i => {
        return i.sysGrade * 100
      })

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
        calculable: true,
        title: {
          text: '其支撑课程目标',
          subtext: ''
        },
        xAxis: {
          type: 'category',
          data: tasksName,
          axisLabel: {
            interval: 0,
            rotate: 40
          }
        },
        yAxis: {
          type: 'value',
          max: 100
        },
        series: {
          name: '成绩',
          data: tasksScores,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#5e128d'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          }
        }
      }
      option && myChart.setOption(option)
    },
    setTargetChart() {
      let chartDom = document.getElementById('targetPie');
      let myChart = echarts.init(chartDom);
      let option;
      let courseTargets = this.targetChartForm.courseTargets
      let dtataNames = []
      let chartData = new Array(courseTargets.length)
      for (let courseTarget of courseTargets) {
        dtataNames.push(courseTarget.course.name)
        chartData.push({
          value: courseTarget.mix,
          name: courseTarget.course.name
        })
      }
      let courseTasks = this.targetChartForm.courseTasks
      let tasksDta = new Array(courseTasks.length)
      console.log(courseTasks)
      for (let courseTask of courseTasks) {
        dtataNames.push(courseTask.describes)
        tasksDta.push({
          value: courseTask.mix,
          name: courseTask.describes
        })
      }
      option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          data: dtataNames,
          top: 'bottom'
        },
        series: [
          {
            name: '课程支撑权重',
            type: 'pie',
            selectedMode: 'single',
            radius: ['5%', '30%'],
            label: {
              position: 'inner',
              fontSize: 14,
            },
            labelLine: {
              show: false
            },
            data: chartData
          },
          {
            name: '课程目标',
            type: 'pie',
            radius: ['45%', '60%'],
            labelLine: {
              length: 30,
            },
            label: {
              formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
              backgroundColor: '#F6F8FC',
              borderColor: '#8C8D8E',
              borderWidth: 1,
              borderRadius: 4,

              rich: {
                a: {
                  color: '#6E7079',
                  lineHeight: 22,
                  align: 'center'
                },
                hr: {
                  borderColor: '#8C8D8E',
                  width: '100%',
                  borderWidth: 1,
                  height: 0
                },
                b: {
                  color: '#4C5058',
                  fontSize: 14,
                  fontWeight: 'bold',
                  lineHeight: 33
                },
                per: {
                  color: '#fff',
                  backgroundColor: '#4C5058',
                  padding: [3, 4],
                  borderRadius: 4
                }
              }
            },
            data: tasksDta
          }
        ]
      };

      option && myChart.setOption(option)
    },
    refreshData() {
      const loadingInstance = Loading.service({
        background: 'rgba(0, 0, 0, 0.7)',
        text: '加载中，请稍后。。。',
        target: 'document.body',
        body: true
      })
      requestByClient(supplierConsumer, 'POST', 'gradRequirement/summaryAll', {}, res => {
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
      requestByClient(supplierConsumer, 'POST', 'gradRequirement/voListAll', {
        year: this.serchForm.year
      }, res => {
        if (res.data.succ) {
          let data = res.data.data
          let targetsName = []
          let max = 0
          for (let datum of data) {
            let children = datum.targets
            if (max < children.length) {
              max = children.length
            }
            let childObj = []
            for (const child of children) {
              childObj.push({
                value: child.id,
                label: child.discribe
              })
            }
            this.options.push({
              value: datum.id,
              label: datum.name,
              children: childObj
            })
          }
          let series = new Array()
          for (let i = 0; i < data.length; i++) {
            for (let j = 0; j < max; j++) {
              if (data[i].targets[j]) {
                targetsName.push(data[i].targets[j].id +':' + data[i].targets[j].discribe)
                series.push({
                  value: (data[i].targets[j].sysGrade * 100).toFixed(2),
                  id: data[i].targets[j].id
                })
              }
            }
          }
          this.setChartData(targetsName, series)
        }
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.historyLabel {
  padding: 20px;
  margin: 20px;
  width: 97%;
  height: 830px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .05)
}

.historyCanvas {
  width: 100%;
  height: 700px;
  padding: 0;
  margin: 0;
}

.targetPie {
  width: 1000px;
  height: 600px;
  padding: 10px;
  display: inline-block;
}

.targetBar {
  margin-left: 50px;
  width: 600px;
  height: 600px;
  padding: 10px;
  display: inline-block;
}
</style>

