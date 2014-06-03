//Copyright (C) 2011-2013 Paul Done . All rights reserved.
//This file is part of the HostMachineStats software distribution. Refer to 
//the file LICENSE in the root of the HostMachineStats distribution.
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
//POSSIBILITY OF SUCH DAMAGE.
package wlhostmachinestats.mbeans;

/**
 * MBean exposing O.S/machine statistics for the machine hosting this WebLogic
 * Server instances. Provides read-only attributes for useful CPU, Memory and 
 * Network related usages statistics.
 *  
 * @see javax.management.MXBean
 */
public interface WLHostMachineStatsMXBean {
	/**
	 * Gets the percentage of processing power (which maybe across a number of
	 * CPUs and Cores) currently being used by all processes on the host 
	 * machine (ie. non-idle processor usage).
	 * 
	 * @return Processor usage percentage
	 */
	public int getProcessorUsagePercent();
	
	/**
	 * Gets the host OS/machine last minute average work load factor.
	 * 
	 * @return Average workload factor for last minute
	 */
	public double getProcessorLastMinuteWorkloadAverage();

	/**
	 * Gets the number of OS processes currently running on the host machine.
	 * 
	 * @return The number of OS processes running
	 */
	public long getNativeProcessesCount();

	/**
	 * Gets the percentage of host machine memory being used by all processes.
	 * 
	 * @return The percentage of physical memory used
	 */
	public int getPhysicalMemoryUsedPercent();

	/**
	 * Gets the percentage of host machine swap being used by all processes.
	 * 
	 * @return The percentage of physical swap used
	 */
	public int getPhysicalSwapUsedPercent();

	/**
	 * Gets the percentage of the total root filesystem partition space, 
	 * currently filled.
	 *  
	 * @return The percentage of root filesystem used
	 */
	public int getRootFilesystemUsedPercent();

	/**
	 * Get the number of cores (including fractions of cores) currently being 
	 * used to run this WebLogic Server JVM process.
	 * 
	 * @return The proportion of cores in use by this JVM
	 */
	public double getJVMInstanceCoresUsed();
	
	/**
	 * Get the amount of physical memory (megabytes) being consumers by this
	 * WebLogic Server JVM process.
	 *  
	 * @return Amount of physical memory used by the JVM in megabytes
	 */
	public long getJVMInstancePhysicalMemoryUsedMegabytes();

	/**
	 * The count of TCP sockets on the host machine in the LISTEN state.
	 * 
	 * @return Count of TCP sockets in LISTEN state
	 */
	public int getTcpListenCount();
	
	/**
	 * The count of TCP sockets on the host machine in the ESTABLISHED state.
	 * 
	 * @return Count of TCP sockets in ESTABLISHED state
	 */
	public int getTcpEstablishedCount();
	
	/**
	 * The count of TCP sockets on the host machine in the TIME-WAIT state.
	 * 
	 * @return Count of TCP sockets in TIME-WAIT state
	 */
	public int getTcpTimeWaitCount();
	
	/**
	 * The count of TCP sockets on the host machine in the CLOSE-WAIT state.
	 * 
	 * @return Count of TCP sockets in CLOSE-WAIT state
	 */
	public int getTcpCloseWaitCount();

	/**
	 * The network interface available on the host machine that is identified 
	 * as the primary interface to monitor by this MBean.
	 * 
	 * @return The name of the network interface being monitored
	 */
	public String getMonitoredNetworkInferfaceName();

	/** 
	 * The primary network inferface's number of packets, in millions, 
	 * received.
	 * 
	 * @return Number of packets received (in millions)
	 */
	public long getNetworkRxMillionPackets();

	/** 
	 * The primary network inferface's number of errors in receiving.
	 * 
	 * @return Number of errors in receiving
	 */
	public long getNetworkRxErrors();

	/** 
	 * The primary network inferface's number of packets dropped in receiving.
	 * 
	 * @return Number of packets dropped in receiving
	 */
	public long getNetworkRxDropped();
	
	/** 
	 * The primary network inferface's number of frames received.
	 * 
	 * @return Number of frames received
	 */
	public long getNetworkRxFrame();
	
	/** 
	 * The primary network inferface's number of overruns in receiving.
	 * 
	 * @return Number of packets overruns in receiving
	 */
	public long getNetworkRxOverruns();

	/** 
	 * The primary network inferface's number of megabytes received.
	 * 
	 * @return Number of megabytes received
	 */
	public long getNetworkRxMegabytes();

	/** 
	 * The primary network inferface's number of packets, in millions, 
	 * transmitted.
	 * 
	 * @return Number of packets transmitted (in millions)
	 */
	public long getNetworkTxMillionPackets();

	/** 
	 * The primary network inferface's number of errors in transmitting.
	 * 
	 * @return Number of errors in transmitting
	 */
	public long getNetworkTxErrors();
	
	/** 
	 * The primary network inferface's number of packets dropped in 
	 * transmitting.
	 * 
	 * @return Number of packets dropped in transmitting
	 */
	public long getNetworkTxDropped(); 

	/** 
	 * The primary network inferface's number of overruns in transmitting.
	 * 
	 * @return Number of packets overruns in transmitting
	 */
	public long getNetworkTxOverruns();

	/** 
	 * The primary network inferface's number of carrier problems in transmitting.
	 * 
	 * @return Number of packets carrier problems in transmitting
	 */
	public long getNetworkTxCarrier(); 

	/** 
	 * The primary network inferface's number of collisions in transmitting.
	 * 
	 * @return Number of packets collisions in transmitting
	 */
	public long getNetworkTxCollisions();

	/** 
	 * The primary network inferface's number of megabytes transmitted.
	 * 
	 * @return Number of megabytes transmitted
	 */
	public long getNetworkTxMegabytes();
	
	/**
	 * The version of the WLHostMachineStats MBean. 
	 * Format: "x.x.x". Example: "0.1.0".
	 * 
	 * @return The version of WLHostMachineStats MBean
	 */
	public String getMBeanVersion();	
}
