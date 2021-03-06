# TCL File Generated by Component Editor 15.0
# Tue Sep 08 15:32:56 CST 2020
# DO NOT MODIFY


# 
# color "color" v1.0
#  2020.09.08.15:32:56
# 
# 

# 
# request TCL package from ACDS 15.0
# 
package require -exact qsys 15.0


# 
# module color
# 
set_module_property DESCRIPTION ""
set_module_property NAME color
set_module_property VERSION 1.0
set_module_property INTERNAL false
set_module_property OPAQUE_ADDRESS_MAP true
set_module_property AUTHOR ""
set_module_property DISPLAY_NAME color
set_module_property INSTANTIATE_IN_SYSTEM_MODULE true
set_module_property EDITABLE true
set_module_property REPORT_TO_TALKBACK false
set_module_property ALLOW_GREYBOX_GENERATION false
set_module_property REPORT_HIERARCHY false


# 
# file sets
# 
add_fileset QUARTUS_SYNTH QUARTUS_SYNTH "" ""
set_fileset_property QUARTUS_SYNTH TOP_LEVEL CPPS
set_fileset_property QUARTUS_SYNTH ENABLE_RELATIVE_INCLUDE_PATHS false
set_fileset_property QUARTUS_SYNTH ENABLE_FILE_OVERWRITE_MODE false
add_fileset_file CPPS.v VERILOG PATH color/CPPS.v TOP_LEVEL_FILE


# 
# parameters
# 
add_parameter PRE STD_LOGIC_VECTOR 6
set_parameter_property PRE DEFAULT_VALUE 6
set_parameter_property PRE DISPLAY_NAME PRE
set_parameter_property PRE TYPE STD_LOGIC_VECTOR
set_parameter_property PRE UNITS None
set_parameter_property PRE ALLOWED_RANGES 0:15
set_parameter_property PRE HDL_PARAMETER true
add_parameter RED STD_LOGIC_VECTOR 0
set_parameter_property RED DEFAULT_VALUE 0
set_parameter_property RED DISPLAY_NAME RED
set_parameter_property RED TYPE STD_LOGIC_VECTOR
set_parameter_property RED UNITS None
set_parameter_property RED ALLOWED_RANGES 0:15
set_parameter_property RED HDL_PARAMETER true
add_parameter R2G STD_LOGIC_VECTOR 1
set_parameter_property R2G DEFAULT_VALUE 1
set_parameter_property R2G DISPLAY_NAME R2G
set_parameter_property R2G TYPE STD_LOGIC_VECTOR
set_parameter_property R2G UNITS None
set_parameter_property R2G ALLOWED_RANGES 0:15
set_parameter_property R2G HDL_PARAMETER true
add_parameter GREEN STD_LOGIC_VECTOR 2
set_parameter_property GREEN DEFAULT_VALUE 2
set_parameter_property GREEN DISPLAY_NAME GREEN
set_parameter_property GREEN TYPE STD_LOGIC_VECTOR
set_parameter_property GREEN UNITS None
set_parameter_property GREEN ALLOWED_RANGES 0:15
set_parameter_property GREEN HDL_PARAMETER true
add_parameter G2B STD_LOGIC_VECTOR 3
set_parameter_property G2B DEFAULT_VALUE 3
set_parameter_property G2B DISPLAY_NAME G2B
set_parameter_property G2B TYPE STD_LOGIC_VECTOR
set_parameter_property G2B UNITS None
set_parameter_property G2B ALLOWED_RANGES 0:15
set_parameter_property G2B HDL_PARAMETER true
add_parameter BLUE STD_LOGIC_VECTOR 4
set_parameter_property BLUE DEFAULT_VALUE 4
set_parameter_property BLUE DISPLAY_NAME BLUE
set_parameter_property BLUE TYPE STD_LOGIC_VECTOR
set_parameter_property BLUE UNITS None
set_parameter_property BLUE ALLOWED_RANGES 0:15
set_parameter_property BLUE HDL_PARAMETER true
add_parameter FIN STD_LOGIC_VECTOR 5
set_parameter_property FIN DEFAULT_VALUE 5
set_parameter_property FIN DISPLAY_NAME FIN
set_parameter_property FIN TYPE STD_LOGIC_VECTOR
set_parameter_property FIN UNITS None
set_parameter_property FIN ALLOWED_RANGES 0:15
set_parameter_property FIN HDL_PARAMETER true
add_parameter WHITE_R INTEGER 765
set_parameter_property WHITE_R DEFAULT_VALUE 765
set_parameter_property WHITE_R DISPLAY_NAME WHITE_R
set_parameter_property WHITE_R TYPE INTEGER
set_parameter_property WHITE_R UNITS None
set_parameter_property WHITE_R ALLOWED_RANGES -2147483648:2147483647
set_parameter_property WHITE_R HDL_PARAMETER true
add_parameter WHITE_G INTEGER 765
set_parameter_property WHITE_G DEFAULT_VALUE 765
set_parameter_property WHITE_G DISPLAY_NAME WHITE_G
set_parameter_property WHITE_G TYPE INTEGER
set_parameter_property WHITE_G UNITS None
set_parameter_property WHITE_G ALLOWED_RANGES -2147483648:2147483647
set_parameter_property WHITE_G HDL_PARAMETER true
add_parameter WHITE_B INTEGER 765
set_parameter_property WHITE_B DEFAULT_VALUE 765
set_parameter_property WHITE_B DISPLAY_NAME WHITE_B
set_parameter_property WHITE_B TYPE INTEGER
set_parameter_property WHITE_B UNITS None
set_parameter_property WHITE_B ALLOWED_RANGES -2147483648:2147483647
set_parameter_property WHITE_B HDL_PARAMETER true


# 
# display items
# 


# 
# connection point clock
# 
add_interface clock clock end
set_interface_property clock clockRate 0
set_interface_property clock ENABLED true
set_interface_property clock EXPORT_OF ""
set_interface_property clock PORT_NAME_MAP ""
set_interface_property clock CMSIS_SVD_VARIABLES ""
set_interface_property clock SVD_ADDRESS_GROUP ""

add_interface_port clock csi_CLK clk Input 1


# 
# connection point clock_reset
# 
add_interface clock_reset reset end
set_interface_property clock_reset associatedClock clock
set_interface_property clock_reset synchronousEdges DEASSERT
set_interface_property clock_reset ENABLED true
set_interface_property clock_reset EXPORT_OF ""
set_interface_property clock_reset PORT_NAME_MAP ""
set_interface_property clock_reset CMSIS_SVD_VARIABLES ""
set_interface_property clock_reset SVD_ADDRESS_GROUP ""

add_interface_port clock_reset csi_reset_n reset_n Input 1


# 
# connection point avalon_slave_0
# 
add_interface avalon_slave_0 avalon end
set_interface_property avalon_slave_0 addressUnits WORDS
set_interface_property avalon_slave_0 associatedClock clock
set_interface_property avalon_slave_0 associatedReset clock_reset
set_interface_property avalon_slave_0 bitsPerSymbol 8
set_interface_property avalon_slave_0 burstOnBurstBoundariesOnly false
set_interface_property avalon_slave_0 burstcountUnits WORDS
set_interface_property avalon_slave_0 explicitAddressSpan 0
set_interface_property avalon_slave_0 holdTime 0
set_interface_property avalon_slave_0 linewrapBursts false
set_interface_property avalon_slave_0 maximumPendingReadTransactions 0
set_interface_property avalon_slave_0 maximumPendingWriteTransactions 0
set_interface_property avalon_slave_0 readLatency 0
set_interface_property avalon_slave_0 readWaitTime 1
set_interface_property avalon_slave_0 setupTime 0
set_interface_property avalon_slave_0 timingUnits Cycles
set_interface_property avalon_slave_0 writeWaitTime 0
set_interface_property avalon_slave_0 ENABLED true
set_interface_property avalon_slave_0 EXPORT_OF ""
set_interface_property avalon_slave_0 PORT_NAME_MAP ""
set_interface_property avalon_slave_0 CMSIS_SVD_VARIABLES ""
set_interface_property avalon_slave_0 SVD_ADDRESS_GROUP ""

add_interface_port avalon_slave_0 avs_chipselect chipselect Input 1
add_interface_port avalon_slave_0 avs_address address Input 5
add_interface_port avalon_slave_0 avs_read read Input 1
add_interface_port avalon_slave_0 avs_readdata readdata Output 32
add_interface_port avalon_slave_0 avs_write write Input 1
add_interface_port avalon_slave_0 avs_writedata writedata Input 32
set_interface_assignment avalon_slave_0 embeddedsw.configuration.isFlash 0
set_interface_assignment avalon_slave_0 embeddedsw.configuration.isMemoryDevice 0
set_interface_assignment avalon_slave_0 embeddedsw.configuration.isNonVolatileStorage 0
set_interface_assignment avalon_slave_0 embeddedsw.configuration.isPrintableDevice 0


# 
# connection point conduit_end_0
# 
add_interface conduit_end_0 conduit end
set_interface_property conduit_end_0 associatedClock clock
set_interface_property conduit_end_0 associatedReset clock_reset
set_interface_property conduit_end_0 ENABLED true
set_interface_property conduit_end_0 EXPORT_OF ""
set_interface_property conduit_end_0 PORT_NAME_MAP ""
set_interface_property conduit_end_0 CMSIS_SVD_VARIABLES ""
set_interface_property conduit_end_0 SVD_ADDRESS_GROUP ""

add_interface_port conduit_end_0 coe_inSQ in_sq Input 1
add_interface_port conduit_end_0 coe_Sctrl s_ctrl Output 2
add_interface_port conduit_end_0 coe_getit get_it Output 1

