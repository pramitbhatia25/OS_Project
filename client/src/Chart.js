import React from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

const ChartComponent = ({ data, color}) => {
  const labels = data.map((_, index) => `Second ${index + 1}`);
  const chartData = {
    labels: labels,
    datasets: [
      {
        label: data[data.length-1] + '%',
        data: data,
        borderColor: color,
        fill: true, // Fill the area beneath the line
        pointRadius: 0, // Remove data points
        pointHoverRadius: 0, // Remove data points on hover

      },
    ],
  };

  const chartOptions = {
    animation: {
      duration: 0, // Disable animations by setting duration to 0
    },
    scales: {
      y: {
        min: 0, // Set minimum value of y-axis
        max: 100, // Set maximum value of y-axis
      },
    },

  };

  
  return <Line data={chartData} options={chartOptions} />;
};

export default ChartComponent;
