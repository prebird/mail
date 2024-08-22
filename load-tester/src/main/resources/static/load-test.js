const loadTest = {
  loadTestSummaryList: []
  ,tpsChart: undefined

  , getLoadTestSummary: (row) => {
    const id = row.getAttribute("data-id");  // 클릭한 행의 id 값 가져오기
    axios.get(`/load-tests/${id}/tps`)
    .then(response => {
      console.log(response.data);
      // loadTest.loadTestSummaryList = response.data;
      loadTest.updateChart(response.data);
    })
    .catch(error => {
      console.error('Error fetching summary:', error);
    });
  }
  , updateChart: (tpsData) => {
    const labels = tpsData.map(item => item.time);
    const tps = tpsData.map(item => item.tps);

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
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          borderColor: 'rgba(75, 192, 192, 1)',
          data: tps,
          fill: false,
          pointRadius: 3, // 점 크기 설정
          pointHoverRadius: 5, // 마우스 오버 시 점 크기 설정
          borderWidth: 2, // 선 두께 설정
        }]
      },
      options: {
        responsive: true,
        scales: {
          x: {
            title: {
              display: true,
              text: 'Elapsed Time (s)'
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
