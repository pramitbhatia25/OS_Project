import React, { useState, useEffect } from 'react';
import axios from 'axios';
import ChartComponent from './Chart';
import './App.css';

const App = () => {
  const [data, setData] = useState({
    item1: [],
    item2: [],
    item3: [],
  });

  useEffect(() => {


    const fetchData = async () => {
      try {
        const response = await fetch('http://localhost:8000/system/info');
        // Transform textData into valid JSON format
        const response_text = await response.text()
        const jsonData = JSON.parse(
          response_text
            // Replace "=" with ":" to convert to valid JSON format
            .replace(/=/g, ':')
            // Wrap keys with double quotes to ensure valid JSON format
            .replace(/(\w+)(?=:)/g, '"$1"')
        );

        const { disk, memory, cpu } = jsonData;

        setData(prevData => ({
          ...prevData,
          item1: [...prevData.item1, cpu],
          item2: [...prevData.item2, memory],
          item3: [...prevData.item3, disk],
        }));
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    const interval = setInterval(fetchData, 1000); // Fetch every second
    return () => clearInterval(interval);

  }, []);

  // useEffect(() => {
  //   const fetchData = async () => {
  //     try {
  //       const response = await axios.get('http://127.0.0.1:5000');
  //       setData(prevData => ({
  //         ...prevData,
  //         item1: [...prevData.item1, response.data.item1],
  //         item2: [...prevData.item2, response.data.item2],
  //         item3: [...prevData.item3, response.data.item3],
  //       }));
  //     } catch (error) {
  //       console.error('Error fetching data:', error);
  //     }
  //   };

  //   const interval = setInterval(fetchData, 1000); // Fetch every second
  //   return () => clearInterval(interval);
  // }, []);

  return (
    <div className="App">
      <div className="charts">
        <div className="chart">
          <h2>CPU Percentage</h2>
          <ChartComponent data={data.item1} color='red' />
        </div>
        <div className="chart">
          <h2>Memory Percentage</h2>
          <ChartComponent data={data.item2} color='blue' />
        </div>
      </div>
      <div className='charts'>
        <div className="chart">
          <h2>Disk Percentage</h2>
          <ChartComponent data={data.item3} color='green' />
        </div>
        <div className="chart">
          <h2>Bandwidth Percentage</h2>
          <ChartComponent data={data.item3} color='purple' />
        </div>
      </div>
    </div>
  );
};

export default App;
