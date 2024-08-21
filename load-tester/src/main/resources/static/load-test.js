const loadTest = {
  loadTestSummaryList: []
  ,tpsChart: undefined

  , getLoadTestSummary: (row) => {
    const id = row.getAttribute("data-id");  // 클릭한 행의 id 값 가져오기
    axios.get(`/load-tests/${id}/summary`)
    .then(response => {
      console.log(response.data);
      // loadTest.loadTestSummaryList = response.data;
      loadTest.updateChart(response.data);
    })
    .catch(error => {
      console.error('Error fetching summary:', error);
    });
  }

  , updateChart: (data) => {
    const labels = data.map(item => item.loopIdx);
    const tpsData = data.map(item => item.tps);

    const ctx = document.getElementById('tpsChart').getContext('2d');
    if (loadTest.tpsChart) {
      loadTest.tpsChart.destroy(); // 이전 차트를 제거합니다.
    }

    loadTest.tpsChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'TPS',
          backgroundColor: 'rgba(54, 162, 235, 0.2)',
          borderColor: 'rgba(54, 162, 235, 1)',
          data: tpsData,
        }]
      },
      options: {
        responsive: true,
        scales: {
          x: {
            title: {
              display: true,
              text: 'Loop Index'
            }
          },
          y: {
            title: {
              display: true,
              text: 'TPS'
            },
            beginAtZero: true
          }
        }
      }
    });
  }
}
