<template>
  <div>
    <div class="historyLabel">
      <el-select v-model="barForm.barcode" placeholder="选择查看商品" filterable clearable @change="getHistoryData()">
        <el-option label="所有商品" value="" />
        <el-option label="商品一" value="6901285991271" />
        <el-option label="商品二" value="6907992822747" />
      </el-select>
      <el-select v-model="barForm.storeId" placeholder="选择查看店铺" filterable clearable @change="getHistoryData()">
        <el-option label="所有店铺" value="" />
        <el-option label="店铺一" value="69" />
        <el-option label="店铺二" value="30" />
      </el-select>
      <el-date-picker
        v-model="barForm.dates"
        type="daterange"
        unlink-panels
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        :picker-options="pickerOptions"
        @change="getHistoryData"
      />
      <div id="historyData" class="historyCanvas" />
      <div id="historyDataHours" class="historyCanvas1" />
    </div>
  </div>
</template>

<script>
import echarts from 'echarts'
import { requestByClient } from '@/utils/HttpUtils'
import { supplierConsumer } from '@/utils/HttpUtils'
export default {
  name: 'Analys',
  data() {
    return {
      data: [],
      value: [0, 23],
      barForm: {
        barcode: '',
        storeId: '',
        dates: ''
      },
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      hours: [],
      counts: [],
      days: [],
      counts1: []
    }
  },
  mounted() {
    this.getHistoryData()
  },
  methods: {
    getHistoryData() {
      if (this.barForm.dates !== null && this.barForm.dates.length > 1) {
        var startDate = this.barForm.dates[0]
        var endDate = this.barForm.dates[1]
      }
      requestByClient(supplierConsumer, 'POST', '/storeBarcode/historyHours', {
        barcode: this.barForm.barcode,
        storeId: this.barForm.storeId,
        startDate: startDate,
        endDate: endDate
      }, res => {
        this.hours = res.data.data.map(i => { return i.hour + ':00' })
        this.counts = res.data.data.map(i => { return i.count })
        this.setChartData(this.hours, this.counts)
      })

      requestByClient(supplierConsumer, 'POST', '/storeBarcode/historyDayAndHours', {
        barcode: this.barForm.barcode,
        storeId: this.barForm.storeId,
        startDate: startDate,
        endDate: endDate
      }, res => {
        this.days = res.data.data.map(i => { return i.date + '点' })
        this.counts1 = res.data.data.map(i => { return i.count })
        this.setChartDataOfHour(this.days, this.counts1)
      })
    },
    setChartData(hours, counts) {
      const chartDom = document.getElementById('historyData')
      const myChart = echarts.init(chartDom)
      var option
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
          text: '时段商品销售量',
          subtext: '单位（件）'
        },
        xAxis: {
          type: 'category',
          data: hours
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '销售量',
          data: counts,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#521893'
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
        }]
      }
      option && myChart.setOption(option)
    },
    setChartDataOfHour(dates, counts) {
      var chartDom1 = document.getElementById('historyDataHours')
      var myChart1 = echarts.init(chartDom1)
      var option1
      option1 = {
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
          text: '历史时段商品销售量',
          subtext: '单位（件）'
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: counts,
          type: 'line',
          itemStyle: {
            normal: {
              color: '#732ebf'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180, 180, 180, 0.2)'
          }
        }]
      }
      option1 && myChart1.setOption(option1)
    }
  }
}
</script>

<style scoped>
.historyLabel{
  padding: 30px;
  margin: 30px;
  width: 95%;
  height: 830px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .05)
}
.historyCanvas{
  width: 100%;
  height: 500px;
  padding: 0;
  margin: 0;
}
.historyCanvas1{
  width: 100%;
  height: 30%;
}
</style>
