<template>
  <div>
    <div class="historyLabel">
      <el-select v-model="serchForm.year" placeholder="选择事件" filterable clearable @change="getList()">
        <el-option label="2021" value="2021"/>
        <el-option label="2020" value="2020"/>
      </el-select>
      <span style="float: right;margin-right: 180px">
        <el-button type="primary" @click="refreshData">刷新数据</el-button>
      </span>
      <div id="historyData" class="historyCanvas"/>
    </div>
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
        year: new Date().getFullYear()
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
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
          let reqs = data.map(i => {
            return i.name
          })
          let target1 = data.map(i => {
            if(i.targets[0]){
              return i.targets[0].sysGrade*100
            }else {
              return 0
            }
          })
          let target2 = data.map(i => {
            if(i.targets[1]){
              return i.targets[1].sysGrade*100
            }else {
              return 0
            }
          })
          let target3 = data.map(i => {
            if(i.targets[2]){
              return i.targets[2].sysGrade*100
            }else {
              return 0
            }
          })
          let target4 = data.map(i => {
            if(i.targets[3]){
              return i.targets[3].sysGrade*100
            }else {
              return 0
            }
          })
          this.setChartData(reqs, target1, target2, target3, target4)
        }
        this.loading = false
      })
    },
    setChartData(targetsName, target1, target2, target3, target4) {
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
            magicType: {show: true, type: ['line', 'bar']},
            restore: {show: true},
            saveAsImage: {show: true}
          }
        },
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
        series: [{
          name: '指标点1',
          data: target1,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#199237'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              {type: 'max', name: '最大值'}
            ]
          },
          markLine: {
            data: [
              {type: 'average', name: '平均值'}
            ]
          }
        }, {
          name: '指标点2',
          data: target2,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#6c1dc4'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              {type: 'max', name: '最大值'}
            ]
          },
          markLine: {
            data: [
              {type: 'average', name: '平均值'}
            ]
          }
        }, {
          name: '指标点3',
          data: target3,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#ac1b77'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              {type: 'max', name: '最大值'}
            ]
          },
          markLine: {
            data: [
              {type: 'average', name: '平均值'}
            ]
          }
        }, {
          name: '指标点4',
          data: target4,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#1b55ac'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              {type: 'max', name: '最大值'}
            ]
          },
          markLine: {
            data: [
              {type: 'average', name: '平均值'}
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
</style>

