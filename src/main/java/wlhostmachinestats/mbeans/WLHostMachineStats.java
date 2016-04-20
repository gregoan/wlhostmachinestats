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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;

//import org.hyperic.sigar.NetInterfaceStat;
//import org.hyperic.sigar.Sigar;

import weblogic.logging.NonCatalogLogger;

import com.sun.management.UnixOperatingSystemMXBean;

/**
 * Implementation of the MBean exposing O.S/machine statistics for the machine
 * hosting this WebLogic Server instances. Provides read-only attributes for 
 * useful CPU, Memory and Network related usages statistics.Use SIGAR JNI/C 
 * libraries under the covers (http://support.hyperic.com/display/SIGAR/Home) 
 * to retrieve specific statistics from host operating system.
 *  
 * @see javax.management.MXBean
 */
@SuppressWarnings("restriction")
public class WLHostMachineStats implements WLHostMachineStatsMXBean, MBeanRegistration {
	
	// Constants
	private static final String WL_HOST_MACHINE_APP_NAME = "WLHostMachineStats";
	private static final String WL_HOST_MACHINE_APP_VERSION = "0.3.0";
	private static final int PERCENT = 100;
	private static final String ROOT_FILESYSTEM_PATH = "/"; 	// What about on Windows and other OSes? "c:\\" ?
	private static final int BYTES_PER_MEGABYTE = 1024*1024;
	private static final int MILLION_UNITS = 1000000;
	private static final String INFC_NAMES_TOKENIZER_PATTERN = ",\\s*";
	
	// Members 
	private final Sigar sigar = new Sigar();
	private final NonCatalogLogger log;
	private final String preferredNetInterfaceName;
	private volatile boolean haveLoggedException = false;
		
	/**
	 * Main constructor
	 * 
	 * @param netInterfaceNames Comma separated list of names of the preferred network interface to try to monitor
	 */
	public WLHostMachineStats(String netInterfaceNames) {
		log = new NonCatalogLogger(WL_HOST_MACHINE_APP_NAME);
		preferredNetInterfaceName = findMatchingNetInterfaceName(netInterfaceNames);
		log.notice("Monitored host network interface: " + preferredNetInterfaceName);
	}
	
/*
	public String getName() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    return osMXBean.getName();
	}

	public String getArch() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    return osMXBean.getArch();
	}

	public String getVersion() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    return osMXBean.getVersion();
	}
*/
	
	public int getAvailableProcessors() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    return osMXBean.getAvailableProcessors();
	}
	
	public double getSystemLoadAverage() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    return osMXBean.getSystemLoadAverage();
	}
	
	public long getCommittedVirtualMemorySizeMegabytes() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getCommittedVirtualMemorySize() / BYTES_PER_MEGABYTE;
	    } else {
	    	return -1;
	    }
	}
	
	public long getFreePhysicalMemorySizeMegabytes() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getFreePhysicalMemorySize() / BYTES_PER_MEGABYTE;
	    } else {
	    	return -1;
	    }
	}

	public long getFreeSwapSpaceSizeMegabytes() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getFreeSwapSpaceSize() / BYTES_PER_MEGABYTE;
	    } else {
	    	return -1;
	    }
	}
	
	public long getMaxFileDescriptorCount() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getMaxFileDescriptorCount();
	    } else {
	    	return -1;
	    }
	}
	
	public long getOpenFileDescriptorCount(){
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getOpenFileDescriptorCount();
	    } else {
	    	return -1;
	    }
	}
	
	public double getProcessCpuLoad() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getProcessCpuLoad();
	    } else {
	    	return -1;
	    }
	}
	
	public long getProcessCpuTime() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getProcessCpuTime();
	    } else {
	    	return -1;
	    }
	}
	
	public double getSystemCpuLoad() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getSystemCpuLoad();
	    } else {
	    	return -1;
	    }
	}

	public long getTotalPhysicalMemorySizeMegabytes() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;	    	
	    	return unixMXBean.getTotalPhysicalMemorySize() / BYTES_PER_MEGABYTE;
	    } else {
	    	return -1;
	    }
	}
	
	public long getTotalSwapSpaceSizeMegabytes() {
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    if(osMXBean instanceof UnixOperatingSystemMXBean) {
	    	UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean)osMXBean;
	    	return unixMXBean.getTotalSwapSpaceSize() / BYTES_PER_MEGABYTE;
	    } else {
	    	return -1;
	    }
	}
	
	/**
	 * Gets the percentage of processing power (which maybe across a number of
	 * CPUs and Cores) currently being used by all processes on the host 
	 * machine (ie. non-idle processor usage).
	 * 
	 * @return Processor usage percentage
	 */
	public int getProcessorUsagePercent() {

		try {
			return (int) (PERCENT * sigar.getCpuPerc().getCombined());
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * Gets the host OS/machine last minute average work load factor.
	 * 
	 * @return Average workload factor for last minute
	 */
	public double getProcessorLastMinuteWorkloadAverage() {

		try {
			return (sigar.getLoadAverage())[0];
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * Gets the number of OS processes currently running on the host machine.
	 * 
	 * @return The number of OS processes running
	 */
	public long getNativeProcessesCount() {

		try {
			return sigar.getProcStat().getTotal();
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * Gets the percentage of host machine memory being used by all processes.
	 * 
	 * @return The percentage of physical memory used
	 */
	public int getPhysicalMemoryUsedPercent() {

		try {
			return (int) sigar.getMem().getUsedPercent();
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * Gets the percentage of host machine swap being used by all processes.
	 * 
	 * @return The percentage of physical swap used
	 */
	public int getPhysicalSwapUsedPercent() {

		try {
			return (int) (PERCENT * ((double)sigar.getSwap().getUsed()) / ((double)sigar.getSwap().getTotal()));
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * Gets the percentage of the total root filesystem partition space, 
	 * currently filled.
	 *  
	 * @return The percentage of root filesystem used
	 */
	public int getRootFilesystemUsedPercent() {

		try {
			return (int) (PERCENT * sigar.getFileSystemUsage(ROOT_FILESYSTEM_PATH).getUsePercent());
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * Get the number of cores (including fractions of cores) currently being 
	 * used to run this WebLogic Server JVM process.
	 * 
	 * @return The proportion of cores in use by this JVM
	 */
	public double getJVMInstanceCoresUsed() {

		try {
			return sigar.getProcCpu(sigar.getPid()).getPercent();
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * Get the amount of physical memory (megabytes) being consumers by this
	 * WebLogic Server JVM process.
	 *  
	 * @return Amount of physical memory used by the JVM in megabytes
	 */
	public long getJVMInstancePhysicalMemoryUsedMegabytes() {

		try {
			return (sigar.getProcMem(sigar.getPid()).getSize() / BYTES_PER_MEGABYTE);
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * The count of TCP sockets on the host machine in the LISTEN state.
	 * 
	 * @return Count of TCP sockets in LISTEN state
	 */
	public int getTcpListenCount() {

		try {
			return sigar.getNetStat().getTcpListen();
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * The count of TCP sockets on the host machine in the ESTABLISHED state.
	 * 
	 * @return Count of TCP sockets in ESTABLISHED state
	 */
	public int getTcpEstablishedCount() {

		try {
			return sigar.getNetStat().getTcpEstablished();
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * The count of TCP sockets on the host machine in the TIME-WAIT state.
	 * 
	 * @return Count of TCP sockets in TIME-WAIT state
	 */
	public int getTcpTimeWaitCount() {

		try {
			return sigar.getNetStat().getTcpTimeWait();
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * The count of TCP sockets on the host machine in the CLOSE-WAIT state.
	 * 
	 * @return Count of TCP sockets in CLOSE-WAIT state
	 */
	public int getTcpCloseWaitCount() {

		try {
			return sigar.getNetStat().getTcpCloseWait();
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/**
	 * The network interface available on the host machine that is identified 
	 * as the primary interface to monitor by this MBean.
	 * 
	 * @return The name of the network interface being monitored
	 */
	public String getMonitoredNetworkInferfaceName() {

		if (preferredNetInterfaceName == null) {
			return "<none>";
		} else {
			return preferredNetInterfaceName;
		}
	}
	
	/** 
	 * The primary network inferface's number of packets, in millions, 
	 * received.
	 * 
	 * @return Number of packets received (in millions)
	 */
	public long getNetworkRxMillionPackets() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return (long)(stat.getRxPackets() / MILLION_UNITS);
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of errors in receiving.
	 * 
	 * @return Number of errors in receiving
	 */
	public long getNetworkRxErrors() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getRxErrors();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of packets dropped in receiving.
	 * 
	 * @return Number of packets dropped in receiving
	 */
	public long getNetworkRxDropped() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getRxDropped();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of frames received.
	 * 
	 * @return Number of frames received
	 */
	public long getNetworkRxFrame() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getRxFrame();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of overruns in receiving.
	 * 
	 * @return Number of packets overruns in receiving
	 */
	public long getNetworkRxOverruns() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getRxOverruns();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of megabytes received.
	 * 
	 * @return Number of megabytes received
	 */
	public long getNetworkRxMegabytes() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return (long)(stat.getRxBytes() / BYTES_PER_MEGABYTE);
			}
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of packets, in millions, 
	 * transmitted.
	 * 
	 * @return Number of packets transmitted (in millions)
	 */
	public long getNetworkTxMillionPackets() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return (long)(stat.getTxPackets() / MILLION_UNITS);
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of errors in transmitting.
	 * 
	 * @return Number of errors in transmitting
	 */
	public long getNetworkTxErrors() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getTxErrors();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of packets dropped in 
	 * transmitting.
	 * 
	 * @return Number of packets dropped in transmitting
	 */
	public long getNetworkTxDropped() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getTxDropped();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of overruns in transmitting.
	 * 
	 * @return Number of packets overruns in transmitting
	 */
	public long getNetworkTxOverruns() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getTxOverruns();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of carrier problems in transmitting.
	 * 
	 * @return Number of packets carrier problems in transmitting
	 */
	public long getNetworkTxCarrier() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getTxCarrier();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of collisions in transmitting.
	 * 
	 * @return Number of packets collisions in transmitting
	 */
	public long getNetworkTxCollisions() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return stat.getTxCollisions();
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}

	/** 
	 * The primary network inferface's number of megabytes transmitted.
	 * 
	 * @return Number of megabytes transmitted
	 */
	public long getNetworkTxMegabytes() {

		try {
			if (preferredNetInterfaceName == null) {
				return -1;
			}
			
			NetInterfaceStat stat = sigar.getNetInterfaceStat(preferredNetInterfaceName);
			
			if (stat == null) {
				return -1;
			} else {
				return (long)(stat.getTxBytes() / BYTES_PER_MEGABYTE);
			}			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return -1;
		}
	}
	
	/**
	 * Pre-register event handler - returns MBean name.
	 * 
	 * @return name
	 */
	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
		return name;
	}

	/**
	 * Post-register event handler - logs that started.
	 * 
	 * @param registrationDone Indicates if registration was completed
	 */
	public void postRegister(Boolean registrationDone) {
		log.notice("WlHostMachineStats MBean initialised");
	}

	/**
	 * Pre-deregister event handler - does nothing
	 * 
	 * @throws Exception Indicates problem is post registration
	 */
	public void preDeregister() throws Exception {
	}

	/**
	 * Post-deregister event handler - logs that stopped
	 */
	public void postDeregister() {
		log.notice("WlHostMachineStats MBean destroyed");
	}

	/**
	 * Only log the SIGAR exception the first time because it may be that most
	 * properties can be read but only on a particular OS so want 
	 * WLHostMachineStats to carry on and not fill up the logs with errors 
	 * continuously.
	 * 
	 * @param e The SIGAR exception that has been caught
	 */
	private void logSigarExceptionIfRequired(Exception e) {
		if (!haveLoggedException) {
			log.error("Unable to read host property using SIGAR library, error: " + e.getMessage());
			e.printStackTrace();
			
			if (e.getCause() != null) {
				e.getCause().printStackTrace();
			}
			
			haveLoggedException = true;
		}
	}

	/**
	 * Compares the list of available network interfaces on the host machine 
	 * with the list of preffered interface names to monitor and returns the 
	 * first match using the assumption that this is the primary network 
	 * interface. 
	 * 
	 * @param preferredNetInterfaceNames Comma separated list of names of the preferred network interface to try to monitor
	 * @return The name of the primary matching available network interface 
	 */
	private String findMatchingNetInterfaceName(String preferredNetInterfaceNames) {

		try {
			String[] availableNetInterfaces = sigar.getNetInterfaceList();

			if ((availableNetInterfaces == null) || (availableNetInterfaces.length <= 0)) {
				return null;
			}
			
			String[] preferredNetInterfaces = null;

			if ((preferredNetInterfaceNames != null) && (preferredNetInterfaceNames.length() > 0)) {
				preferredNetInterfaces = preferredNetInterfaceNames.split(INFC_NAMES_TOKENIZER_PATTERN);
			}

			// If no preferred interface names specified, just go with name of
			// first actual available interface on host machine  
			if ((preferredNetInterfaces == null) || (preferredNetInterfaces.length <= 0)) {
				return availableNetInterfaces[0];
			}
			
			// Loop thru list of preferred interface names trying to find 
			// first match with the name of one of an actual available 
			// interface on the host machine
			for (String preferredNetInterface : preferredNetInterfaces) {
				for (String availableNetInterface : availableNetInterfaces) {
					if (preferredNetInterface.equalsIgnoreCase(availableNetInterface)) {
						return availableNetInterface;
					}
				}
			}
			
			// If no matches, just go with name of first available interface 
			return availableNetInterfaces[0];			
		} catch (Exception e) {
			logSigarExceptionIfRequired(e);
			return null;
		}
	}

	/**
	 * The version of the WLHostMachineStats MBean. 
	 * Format: "x.x.x". Example: "0.1.0".
	 * 
	 * @return The version of WLHostMachineStats MBean
	 */
	public String getMBeanVersion() {
		return WL_HOST_MACHINE_APP_VERSION;
	}
}
