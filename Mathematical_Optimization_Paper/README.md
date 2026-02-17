# Electric Bus Network Optimization (ILP) - Group Project

## Overview

This project implements the Opt-Fast-Char2 model to optimize the transition from conventional to electric bus fleets. Using Integer Linear Programming (ILP), the system determines the best locations for fast-charging stations and manages bus assignments to maximize passenger capacity within strict budget and power constraints.

The project represents an implementation of the proposed solution found in the following paper: https://www.sciencedirect.com/science/article/abs/pii/S1366554523000534.

Project documents and codebase were developed as a group project together with my university colleague: **Francesco Rumiz**.

## Key Features

  - **Linear Modeling:** The implementation uses an Integer Linear Programming (ILP) approach for faster, more practical solving.
  - **System Complexity:** The model accounts for multiple bus types, various charging technologies, and the constraints of the electrical power grid.
  - **Realistic Constraints:**
      - *Capital & Variable Costs:* Limits on the total investment available for new buses and charging stations.
      - *Power Demand:* Ensures that the electric power station capacity at any given stop can satisfy the requirements of all connected chargers.
      - *Traffic Intervals:* Maintains standard bus service intervals while accounting for necessary charging time.

## Technical Implementation

  - **Data Generation:** The project uses a mix of real-world transport data and self-constructed datasets to simulate realistic urban environments.
  - **Scalability Analysis:** Includes an experimental setup to test how the solver performs (runtime and memory) as the number of routes, stops, and budget constraints increase.

