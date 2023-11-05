/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamixel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author anfv
 */
public class DynamixelController {

    private final static short ADDR_CW_ANGLE_LIMIT = 6;
    private final static short ADDR_CCW_ANGLE_LIMIT = 8;
    private final static byte ADDR_TEMPERATURE_LIMIT = (byte) 11;
    private final static byte ADDR_MIN_VOLTAGE_LIMIT = (byte) 12;
    private final static byte ADDR_MAX_VOLTAGE_LIMIT = (byte) 13;
    private final static short ADDR_MAX_TORQUE = 14;
    private final static short ADDR_MX_TORQUE_ENABLE = 24;   // Control table address is different in Dynamixel model
    private final static short ADDR_MX_GOAL_POSITION = 30;
    private final static short ADDR_MX_MOVING_SPEED = 32;
    private final static short ADDR_MX_PRESENT_POSITION = 36;

    private final static short LEN_MX_GOAL_POSITION = 2; // Data Byte Length

    private final static int PROTOCOL_VERSION = 1;                   // See which protocol version is used in the Dynamixel

    // Default setting
    private final static byte DEFAULT_DXL_ID = 5;                   // Dynamixel ID: 1
    private final static byte BROADCAST_ID = (byte) 254;
    private final static int USB2AX_ID = 253;    //It is a byte, but as there are not unsigned bytes, better to cast when used
    private final static int BAUDRATE = 1000000;
    private static int port_num;
    private final static String DEVICENAME = "COM3";      // Check which port is being used on your controller
    // ex) Windows: "COM1"   Linux: "/dev/ttyUSB0" Mac: "/dev/tty.usbserial-*"

    private final static byte TORQUE_ENABLE = 1;                   // Value for enabling the torque
    private final static byte TORQUE_DISABLE = 0;                   // Value for disabling the torque
    private final static short DXL_MINIMUM_POSITION_VALUE = 300;                 // Dynamixel will rotate between this value
    private final static short DXL_MAXIMUM_POSITION_VALUE = 700;                // and this value (note that the Dynamixel would not move when the position value is out of movable range. Check e-manual about the range of the Dynamixel you use.)
    private final static int DXL_MOVING_STATUS_THRESHOLD = 10;                  // Dynamixel moving status threshold

    private final static int COMM_SUCCESS = 0;                   // Communication Success result value
    private final static int COMM_TX_FAIL = -1001;               // Communication Tx Failed

    private static Dynamixel dynamixel;
    private int group_target_pos;
    private boolean connected = false;

    public DynamixelController() {
        // Initialize Dynamixel class for java
        dynamixel = new Dynamixel();

        // Initialize PortHandler Structs
        int port_num = dynamixel.portHandler(DEVICENAME);

        // Initialize PacketHandler Structs
        dynamixel.packetHandler();

        // Initialize Groupsyncwrite instance
        group_target_pos = dynamixel.groupSyncWrite(port_num, PROTOCOL_VERSION, ADDR_MX_GOAL_POSITION, LEN_MX_GOAL_POSITION);

        // Open port
        if (dynamixel.openPort(port_num)) {
            System.out.println("Succeeded to open the port!");
            connected = true;
        } else {
            System.out.println("Failed to open the port!");
            return;
        }

        // Set port baudrate
        if (dynamixel.setBaudRate(port_num, BAUDRATE)) {
            System.out.println("Succeeded to change the baudrate!");
        } else {
            System.out.println("Failed to change the baudrate!");
            return;
        }
        setsetAngleLimit();
    }

    boolean pingMotor(byte id) {
        // Try to ping the Dynamixel, Get Dynamixel model number
        int dxl_comm_result = COMM_TX_FAIL;
        byte dxl_error = 0;
        int dxl_model_number;                                     // Dynamixel model number
        dxl_model_number = dynamixel.pingGetModelNum(port_num, PROTOCOL_VERSION, id);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            //System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result));
            return false;
        } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
            //System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error));
            return false;
        } else {
            System.out.printf("[ID: %d] ping Succeeded. Dynamixel model number : %d\n", Byte.toUnsignedInt(id), dxl_model_number);
            return true;
        }
    }

    List<Byte> scan() {
        System.out.println("Starting a scan to find Dynamixel motors...");
        List<Byte> ids = new ArrayList<>();

        for (int i = 0; i < USB2AX_ID; i++) {
            if (pingMotor((byte) i)) {
                ids.add((byte) i);
                if (i == 0) {
                    System.out.println("Warning, motors id should be higher than 0 in EDHMOR/EMERGE!");
                }
            }
        }
        System.out.printf("Scan ended, found %d motors\n", ids.size());
        return ids;
    }

    // Enable or Disable Dynamixel Torque
    protected boolean enableTorque(byte id, boolean enable) {
        int dxl_comm_result = COMM_TX_FAIL;
        byte dxl_error = 0;
        byte enableByte = TORQUE_DISABLE;
        if (enable) {
            enableByte = TORQUE_ENABLE;
        }

        dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_TORQUE_ENABLE, enableByte);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
            System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        } else {
            if (enable) {
                System.out.println("Dynamixel has been successfully enabled torque, motor: " + Byte.toUnsignedInt(id));
            } else {
                System.out.println("Dynamixel has been successfully disabled torque, motor: " + Byte.toUnsignedInt(id));
            }
            return true;
        }
    }

    boolean setTargetPos(byte id, short goalPos) {
        // Write goal position
        int dxl_comm_result = COMM_TX_FAIL;
        byte dxl_error = 0;
        dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_GOAL_POSITION, goalPos);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
            System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        }
        return true;
    }

    public boolean setMaxMovingSpeed() {
        return setMovingSpeed(BROADCAST_ID, (short) 0);
    }

    public boolean setMovingSpeed(double movingSpeed) {
        //Speed in rpm
        return setMovingSpeed(BROADCAST_ID, movingSpeed);
    }

    public boolean setMovingSpeed(byte id, double movingSpeed) {
        //Speed in rpm, change to right units
        short movingSpeedShort = (short) (movingSpeed / 0.111);

        //Ste to maximum if necessary
        if (movingSpeedShort > 1023) {
            movingSpeedShort = 0;
        }
        System.out.println("Setting max speed to " + movingSpeedShort);
        int dxl_comm_result = COMM_TX_FAIL;
        byte dxl_error = 0;
        dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_MOVING_SPEED, movingSpeedShort);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
            System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        }
        return true;
    }

    boolean setTargetPosBulkWrite(byte id, short pos) {
        Boolean dxl_addparam_result = false;                        // AddParam result
        // Add Dynamixel#1 goal position value to the Syncwrite storage
        dxl_addparam_result = dynamixel.groupSyncWriteAddParam(group_target_pos, id, pos, LEN_MX_GOAL_POSITION);
        if (dxl_addparam_result != true) {
            System.out.printf("[ID: %d] groupSyncWrite addparam failed\n", Byte.toUnsignedInt(id));
        }
        return dxl_addparam_result;
    }

    boolean sendTargetPos() {
        // Syncwrite goal position
        int dxl_comm_result = COMM_TX_FAIL;
        dynamixel.groupSyncWriteTxPacket(group_target_pos);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result));
        }

        // Clear syncwrite parameter storage
        dynamixel.groupSyncWriteClearParam(group_target_pos);
        return (dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) == COMM_SUCCESS;
    }

    short readCurrentPos(byte id) {
        // Read present position
        int dxl_comm_result = COMM_TX_FAIL;
        byte dxl_error = 0;
        short dxl_present_position = 0;
        dxl_present_position = dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_PRESENT_POSITION);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result) + ", motor id: " + Byte.toUnsignedInt(id));
            dxl_present_position = -1;
        } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
            System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error) + ", motor id: " + Byte.toUnsignedInt(id));
            dxl_present_position = -1;
        }

        System.out.printf("[ID: %d]  PresPos:%d\n", id, dxl_present_position);
        return dxl_present_position;
    }

    final boolean setsetAngleLimit() {
        return setAngleLimit(BROADCAST_ID);
    }

    final boolean setAngleLimit(byte id) {
        // +- 90 degrees of movement
        short angleLimitCW = 512 - 315;
        short angleLimitCCW = 512 + 315;
        int dxl_comm_result = COMM_TX_FAIL;
        byte dxl_error = 0;
        boolean noError = true;
        dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_CW_ANGLE_LIMIT, angleLimitCW);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result) + ", motor id: " + Byte.toUnsignedInt(id));
            noError = false;
        } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
            System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error) + ", motor id: " + Byte.toUnsignedInt(id));
            noError = false;
        }
        dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_CCW_ANGLE_LIMIT, angleLimitCCW);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
            System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
            System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error) + ", motor id: " + Byte.toUnsignedInt(id));
            return false;
        }
        return noError;
    }

    public void close() {
        if (connected) {
            enableTorque(BROADCAST_ID, false);
            closePort();    // Close port
            System.out.println("Closing serial communications...");
        }
    }

    private void closePort() {
        // Close port
        dynamixel.closePort(port_num);
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

}
